package com.kotlin.biteback.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kotlin.biteback.data.model.RestaurantRating
import com.kotlin.biteback.data.model.RestaurantReview
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class RestaurantRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getWeeklyAverageRatings(): List<RestaurantRating> {
        val calendar = Calendar.getInstance()

        // Inicio del mes (día 1, a las 00:00)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.time

        // Fin del mes (último día, a las 23:59)
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.SECOND, -1)
        val endOfMonth = calendar.time

        val snapshot = db.collection("restaurant_reviews")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .await()

        val reviews = snapshot.documents.mapNotNull { doc ->
            val name = doc.getString("restaurant_name") ?: "Desconocido"
            val score = doc.getDouble("review_score") ?: -1.0
            val timestamp = doc.getTimestamp("timestamp")

            if (name.isNotBlank() && score >= 0) {
                RestaurantReview(name, score, timestamp)
            } else {
                Log.e("DEBUG", "Documento inválido: ${doc.id} - Datos: $doc")
                null
            }
        }



        return reviews
            .groupBy { it.restaurantName }
            .map { (restaurant, scores) ->
                RestaurantRating(restaurant, scores.map { it.reviewScore }.average())
            }
    }

}