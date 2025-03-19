package com.kotlin.biteback.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.biteback.data.model.Product
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class BusinessRepository(private val locationRepo: LocationRepository) {

    private val db = FirebaseFirestore.getInstance()
    private val businessCollection = db.collection("business")


    suspend fun getNearbyProducts(maxDistanceKm: Double): List<Map<String, Any>> {
        val userLocation = locationRepo.getLastKnownLocation() ?: return emptyList()
        val userLat = userLocation.latitude
        val userLon = userLocation.longitude

        return try {
            val snapshot = businessCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val businessLat = doc.getDouble("latitude") ?: return@mapNotNull null
                val businessLon = doc.getDouble("longitude") ?: return@mapNotNull null

                // Calcular distancia entre el usuario y el negocio
                val distance = haversine(userLat, userLon, businessLat, businessLon)

                if (distance <= maxDistanceKm) {
                    val businessData = doc.data ?: return@mapNotNull null
                    val filteredProducts =
                        (businessData["products"] as? List<Map<String, Any>>)?.filterNotNull()
                    businessData + ("filteredProducts" to filteredProducts)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        } as List<Map<String, Any>>
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Radio de la Tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // Distancia en km
    }


    suspend fun getProductsForBusiness(businessId: String): List<Product> {
        return try {
            val snapshot = businessCollection.document(businessId)
                .collection("products")
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(Product::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun createProductForBusiness(businessId: String, product: Product): String? {
        return try {
            val documentReference = businessCollection.document(businessId)
                .collection("products")
                .add(product)
                .await()
            documentReference.id
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateProductForBusiness(businessId: String, productId: String, product: Product): Boolean {
        return try {
            businessCollection.document(businessId)
                .collection("products")
                .document(productId)
                .set(product)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteProductForBusiness(businessId: String, productId: String): Boolean {
        return try {
            businessCollection.document(businessId)
                .collection("products")
                .document(productId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
