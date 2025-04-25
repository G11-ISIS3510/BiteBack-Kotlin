package com.kotlin.biteback.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kotlin.biteback.data.model.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShoppingCartRepository {

    private val db = FirebaseFirestore.getInstance()

    fun registerPurchase(
        products: List<Product>,
        quantityMap: Map<String, Int>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val date = Date()
        val dayOfWeek = SimpleDateFormat("EEEE", Locale("es", "ES")).format(date).replaceFirstChar { it.uppercase() }
        val purchaseProducts = products.map {
            val quantity = quantityMap[it.id] ?: 1
            mapOf(
                "id" to it.id,
                "name" to it.name,
                "price" to it.price,
                "discount" to it.discount,
                "quantity" to quantity
            )
        }

        val totalItems = purchaseProducts.sumOf { it["quantity"] as Int }

        val totalPrice = products.sumOf {
            val quantity = quantityMap[it.id] ?: 1
            val discountedPrice = it.price.toInt() - (it.price.toInt() * it.discount.toInt() / 100)
            discountedPrice * quantity
        }

        val purchase = hashMapOf(
            "timestamp" to FieldValue.serverTimestamp(),
            "date" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date),
            "time" to SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date),
            "day_of_week" to dayOfWeek,
            "total_items" to totalItems,
            "total_price" to totalPrice,
            "products" to purchaseProducts
        )

        FirebaseFirestore.getInstance()
            .collection("purchases")
            .add(purchase)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

}