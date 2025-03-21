package com.kotlin.biteback.ui.restaurantReviews
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.ui.components.ProductCard

@Composable
fun RestaurantReviews(navController: NavController, viewModel: RestaurantReviewsViewModel = viewModel() ) {

    Column {
        Text("RestaurantReview")
    }
    val ratings by viewModel.restaurantRatings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWeeklyRatings()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Puntajes Semanales", style = MaterialTheme.typography.titleLarge)

        ratings.forEach { rating ->
            Text(text = "${rating.restaurantName}: ${String.format("%.2f", rating.averageScore)}")
        }
    }
}