package com.kotlin.biteback.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val points: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
