package com.listify.data.repository

import com.listify.data.remote.api.FakeStoreApi
import com.listify.data.remote.dto.toDomain
import com.listify.domain.model.Product
import com.listify.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: FakeStoreApi
) : ProductRepository {

    override suspend fun getProducts(limit: Int): Result<List<Product>> = runCatching {
        try {
            api.getProducts(limit = limit).map { it.toDomain() }
        } catch (e: Exception) {
            // API unavailable — use local fallback
            FallbackProducts.items.take(limit)
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> = runCatching {
        try {
            api.getProductById(id).toDomain()
        } catch (e: Exception) {
            FallbackProducts.items.firstOrNull { it.id == id }
                ?: throw NoSuchElementException("Product $id not found")
        }
    }

    override suspend fun getProductsByCategory(category: String, limit: Int): Result<List<Product>> = runCatching {
        try {
            api.getProductsByCategory(category, limit).map { it.toDomain() }
        } catch (e: Exception) {
            FallbackProducts.items.filter { it.category == category }.take(limit)
        }
    }

    override suspend fun getCategories(): Result<List<String>> = runCatching {
        try {
            api.getCategories()
        } catch (e: Exception) {
            FallbackProducts.categories
        }
    }
}
