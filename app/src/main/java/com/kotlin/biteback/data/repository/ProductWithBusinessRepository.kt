package com.kotlin.biteback.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.biteback.data.model.ProductWithBusiness
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductWithBusinessRepository {
    val db = FirebaseFirestore.getInstance()

    suspend fun fetchBusinesses(): Map<String, String> = suspendCoroutine { continuation ->
        db.collection("business").get()
            .addOnSuccessListener { documents ->
                val businessMap = mutableMapOf<String, String>()
                for (document in documents) {
                    val businessId = document.id
                    val businessName = document.getString("name") ?: "Desconocido"
                    businessMap[businessId] = businessName
                }
                continuation.resume(businessMap)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    suspend fun fetchProducts(businessMap: Map<String, String>): List<ProductWithBusiness> = suspendCoroutine { continuation ->
        db.collection("products").get()
            .addOnSuccessListener { documents ->
                val productList = mutableListOf<ProductWithBusiness>()
                for (document in documents) {
                    val product = ProductWithBusiness(
                        businessId = document.getString("businessId") ?: "",
                        businessName = businessMap[document.getString("businessId")] ?: "Desconocido",
                        category = document.getString("category") ?: "",
                        categoryImage = document.getString("categoryImage") ?: "",
                        description = document.getString("description") ?: "",
                        discount = document.getDouble("discount") ?: 0.0,
                        expirationDate = document.getTimestamp("expirationDate")?.toDate()?.time ?: 0L,
                        id = document.id,
                        image = document.getString("image") ?: "",
                        name = document.getString("name") ?: "",
                        price = document.getDouble("price") ?: 0.0
                    )
                    productList.add(product)
                }
                continuation.resume(productList)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

}