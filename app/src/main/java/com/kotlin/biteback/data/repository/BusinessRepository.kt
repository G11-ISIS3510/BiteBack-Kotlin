package com.kotlin.biteback.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.biteback.data.model.Product
import kotlinx.coroutines.tasks.await


class BusinessRepository {

    private val db = FirebaseFirestore.getInstance()
    private val businessCollection = db.collection("business")

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
