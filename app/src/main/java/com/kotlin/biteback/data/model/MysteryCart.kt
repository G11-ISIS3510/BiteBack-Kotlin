package com.kotlin.biteback.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MysteryCart(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val isMysteryBox: Boolean = false,
    val contents: List<ProductWithBusiness> = emptyList()
)
