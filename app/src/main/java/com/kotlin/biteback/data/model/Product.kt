package com.kotlin.biteback.data.model

data class Product(
    val businessId: String = "",
    val category: String = "",
    val categoryImage: String = "",
    val description: String = "",
    val discount: Double = 0.0,
    val expirationDate: Long = 0L,
    val id: String = "",
    val image: String = "",
    val name: String = "",
    val price: Double = 0.0
)
