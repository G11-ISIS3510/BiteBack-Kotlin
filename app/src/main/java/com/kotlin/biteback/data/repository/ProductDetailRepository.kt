package com.kotlin.biteback.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.kotlin.biteback.data.model.Product
import com.google.firebase.Timestamp
import android.util.LruCache
import com.kotlin.biteback.data.model.Business

class ProductDetailRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productCache = LruCache<String, Product>(10)

    suspend fun getBusinessById(businessId: String): Business? {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("business")
            .document(businessId)
            .get()
            .await()

        return snapshot.toObject(Business::class.java)
    }


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
        productCache.get(productId)?.let {
            println("[CACHE] Producto $productId obtenido desde cache")
            return it
        }

        return try {
            val snapshot = db.collection("products").document(productId).get().await()
            val data = snapshot.data

            if (data != null) {
                val product = Product(
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

                productCache.put(productId, product)
                println("[FIRESTORE] Producto $productId cacheado")
                product
            } else {
                println("Producto $productId no encontrado")
                null
            }
        } catch (e: Exception) {
            println("Error obteniendo producto: ${e.message}")
            null
        }
    }
}
