package com.listify.monitoring

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CrashReporter wraps Firebase Crashlytics.
 * In production: replace Log calls with FirebaseCrashlytics.getInstance().*
 * Kept dependency-free here so the project compiles before google-services.json is added.
 */
@Singleton
class CrashReporter @Inject constructor() {

    fun recordException(throwable: Throwable) {
        Log.e("CrashReporter", "Exception recorded: ${throwable.message}", throwable)
        // FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    fun log(message: String) {
        Log.d("CrashReporter", message)
        // FirebaseCrashlytics.getInstance().log(message)
    }

    fun setUserContext(userId: String, email: String) {
        Log.d("CrashReporter", "User context: $userId / $email")
        // FirebaseCrashlytics.getInstance().setUserId(userId)
        // FirebaseCrashlytics.getInstance().setCustomKey("email", email)
    }
}
