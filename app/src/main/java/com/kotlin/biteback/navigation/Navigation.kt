package com.kotlin.biteback.navigation


import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.kotlin.biteback.ui.home.Home
import com.kotlin.biteback.ui.productDetail.ProductDetailScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable("home") { Home(navController) }
        composable("productDetail") { ProductDetailScreen(navController) }
    }
}
