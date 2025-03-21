package com.kotlin.biteback.data.model

import com.google.firebase.Timestamp
data class RestaurantReview(
    val restaurantName: String = "",
    val reviewScore: Double = 0.0,
    val timestamp: Timestamp? = Timestamp.now()
)

