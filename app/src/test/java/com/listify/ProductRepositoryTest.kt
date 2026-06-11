package com.listify.data.repository

import com.listify.data.remote.api.FakeStoreApi
import com.listify.data.remote.dto.ProductDto
import com.listify.data.remote.dto.RatingDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProductRepositoryTest {

    private val api: FakeStoreApi = mockk()
    private lateinit var repository: ProductRepositoryImpl

    private val fakeDto = ProductDto(1, "Shirt", 29.99, "Desc", "clothing", "url", RatingDto(4.5, 100))

    @Before
    fun setUp() { repository = ProductRepositoryImpl(api) }

    @Test
    fun `getProducts maps DTOs to domain models`() = runTest {
        coEvery { api.getProducts(any(), any()) } returns listOf(fakeDto)
        val result = repository.getProducts()
        assertTrue(result.isSuccess)
        val products = result.getOrThrow()
        assertEquals(1, products.size)
        assertEquals("Shirt", products[0].title)
        assertEquals(29.99, products[0].price, 0.01)
    }

    @Test
    fun `getProducts returns fallback data on network exception`() = runTest {
        coEvery { api.getProducts(any(), any()) } throws RuntimeException("Network error")
        val result = repository.getProducts()
        // Now returns fallback data, not failure
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isNotEmpty())
    }

    @Test
    fun `getProductById returns correct product`() = runTest {
        coEvery { api.getProductById(1) } returns fakeDto
        val result = repository.getProductById(1)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().id)
    }

    @Test
    fun `getProductById falls back to local data when API fails`() = runTest {
        coEvery { api.getProductById(any()) } throws RuntimeException("404")
        val result = repository.getProductById(1)
        // Fallback products have id=1, so should succeed
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getCategories returns list of strings`() = runTest {
        coEvery { api.getCategories() } returns listOf("electronics", "clothing")
        val result = repository.getCategories()
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
    }

    @Test
    fun `getCategories falls back to local categories on failure`() = runTest {
        coEvery { api.getCategories() } throws RuntimeException("error")
        val result = repository.getCategories()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isNotEmpty())
    }

    @Test
    fun `getProductsByCategory filters correctly`() = runTest {
        coEvery { api.getProductsByCategory("clothing", any()) } returns listOf(fakeDto)
        val result = repository.getProductsByCategory("clothing")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().all { it.category == "clothing" })
    }
}
