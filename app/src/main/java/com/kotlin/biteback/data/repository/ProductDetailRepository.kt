package com.kotlin.biteback.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.kotlin.biteback.data.model.Product
import com.google.firebase.Timestamp

class ProductDetailRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getBusinessName(businessId: String): String {
        return try {
            val businessDoc = db.collection("business").document(businessId).get().await()

            if (businessDoc.exists()) {
                val name = businessDoc.getString("name") ?: "Nombre no encontrado"
                name
            } else {
                println("⚠️ Negocio no encontrado en Firestore")
                "Desconocido"
            }
        } catch (e: Exception) {
            println("🔥 Error al obtener nombre de negocio: ${e.message}")
            "Desconocido"
        }
    }

    suspend fun getProductById(productId: String): Product? {
        val snapshot = db.collection("products").document(productId).get().await()
        val data = snapshot.data

        return if (data != null) {
            Product(
                id = snapshot.id,
                name = data["name"] as? String ?: "",
                description = data["description"] as? String ?: "",
                price = (data["price"] as? Number)?.toDouble() ?: 0.0,
                discount = (data["discount"] as? Number)?.toDouble() ?: 0.0,
                category = data["category"] as? String ?: "",
                image = data["image"] as? String ?: "",
                businessId = data["businessId"] as? String ?: "",
                expirationDate = (data["expirationDate"] as? Timestamp)?.seconds ?: 0L
            )
        } else {
            null
        }
    }
}
