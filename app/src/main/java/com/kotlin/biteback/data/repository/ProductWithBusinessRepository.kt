package com.kotlin.biteback.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.biteback.data.local.AppDatabase
import com.kotlin.biteback.data.local.entitites.ProductEntity
import com.kotlin.biteback.data.model.ProductWithBusiness
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductWithBusinessRepository(context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val localDb = AppDatabase.getInstance(context).productDao()

    suspend fun fetchAndCacheProducts(): List<ProductWithBusiness> = suspendCoroutine { continuation ->
        db.collection("business").get().addOnSuccessListener { businesses ->
            val businessMap = mutableMapOf<String, String>()
            for (doc in businesses) {
                businessMap[doc.id] = doc.getString("name") ?: "Desconocido"
            }

            db.collection("products").get().addOnSuccessListener { products ->
                val productList = products.map { doc ->
                    ProductWithBusiness(
                        businessId = doc.getString("businessId") ?: "",
                        businessName = businessMap[doc.getString("businessId")] ?: "Desconocido",
                        category = doc.getString("category") ?: "",
                        categoryImage = doc.getString("categoryImage") ?: "",
                        description = doc.getString("description") ?: "",
                        discount = doc.getDouble("discount") ?: 0.0,
                        expirationDate = doc.getTimestamp("expirationDate")?.toDate()?.time ?: 0L,
                        id = doc.id,
                        image = doc.getString("image") ?: "",
                        name = doc.getString("name") ?: "",
                        price = doc.getDouble("price") ?: 0.0
                    )
                }

                // Upload Cache from Data Class model to Entity products
                CoroutineScope(Dispatchers.IO).launch {
                    localDb.clearAll()
                    localDb.insertAll(productList.map { it.toEntity() })
                }

                continuation.resume(productList)
            }
        }.addOnFailureListener { continuation.resumeWithException(it) }
    }
    // Get Entity products to Data Class model
    fun getCachedProducts(): Flow<List<ProductWithBusiness>> {
        return localDb.getAllProducts().map { list -> list.map { it.toDomain() } }
    }

    fun ProductWithBusiness.toEntity() = ProductEntity(
        id, name, businessId, businessName, category,
        categoryImage, description, discount, expirationDate,
        image, price
    )

    fun ProductEntity.toDomain() = ProductWithBusiness(
        businessId = businessId,
        businessName = businessName,
        category = category,
        categoryImage = categoryImage,
        description = description,
        discount = discount,
        expirationDate = expirationDate,
        id = id,
        image = image,
        name = name,
        price = price
    )
}