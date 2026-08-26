package com.example.shopping.presentation.utils

import kotlin.random.Random

object RatingHelper {
    // Unique seed generated each time app is opened/started
    private val sessionSeed = Random(System.currentTimeMillis()).nextInt(100000)
    private val ratingCache = mutableMapOf<String, String>()

    private val possibleRatings = listOf(
        "4.0", "4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9", "5.0"
    )

    fun getRatingForProduct(productId: String): String {
        return ratingCache.getOrPut(productId) {
            val key = if (productId.isNotBlank()) productId else "default_prod"
            val hash = kotlin.math.abs(key.hashCode() + sessionSeed)
            possibleRatings[hash % possibleRatings.size]
        }
    }
}
