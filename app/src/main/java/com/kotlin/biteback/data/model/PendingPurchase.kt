package com.kotlin.biteback.data.model

data class PendingPurchase(
    val products: List<Product>,
    val quantities: Map<String, Int>,
    val timestamp: Long
)