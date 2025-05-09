package com.kotlin.biteback.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val email: String,
    val password: String,
    val timestamp: Long = System.currentTimeMillis()
)
