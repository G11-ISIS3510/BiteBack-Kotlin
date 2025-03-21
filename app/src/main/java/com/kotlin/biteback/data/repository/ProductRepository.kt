package com.kotlin.biteback.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.biteback.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")

    fun getProducts(): Flow<List<Product>> = flow {
        try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.documents.mapNotNull { document ->
                Product(
                    businessId = document.getString("businessId") ?: "",
                    category = document.getString("category") ?: "",
                    categoryImage = document.getString("categoryImage") ?: "",
                    description = document.getString("description") ?: "",
                    discount = document.getDouble("discount") ?: 0.0,
                    expirationDate = document.getTimestamp("expirationDate")?.toDate()?.time ?: 0L,
                    id = document.getString("id") ?: "",
                    image = document.getString("image") ?: "",
                    name = document.getString("name") ?: "",
                    price = document.getDouble("price") ?: 0.0
                )
            }
            emit(products)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}