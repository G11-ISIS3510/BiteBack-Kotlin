package com.kotlin.biteback.data.local.entitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val businessId: String,
    val businessName: String,
    val category: String,
    val categoryImage: String,
    val description: String,
    val discount: Double,
    val expirationDate: Long,
    val image: String,
    val price: Double
)
