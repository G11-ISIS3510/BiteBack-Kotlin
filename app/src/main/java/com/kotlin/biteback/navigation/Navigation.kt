package com.kotlin.biteback.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.kotlin.biteback.ui.home.Home
import com.kotlin.biteback.ui.login.Login
import com.kotlin.biteback.ui.productDetail.ProductDetailScreen
import com.kotlin.biteback.ui.register.Register
import android.content.Context
import com.kotlin.biteback.ui.restaurantReviews.RestaurantReviews

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        // Pantalla de Login
        composable("login") { Login(navController, context) }

        // **Pantalla de Registro** (La agregamos aquí)
        composable("register") { Register(navController, context) }

        // Pantalla de Home (Solo accesible después de iniciar sesión)
        composable("home") {
            Home(navController, onNotificationClick = { /* Acción al hacer clic en notificaciones */ })
        }

        composable("restaurantReviews") {
            RestaurantReviews(navController)
            }

        // Pantalla de Detalle del Producto
        composable("productDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            println("🔍 Navegando a ProductDetail con ID: $productId")
            ProductDetailScreen(navController, productId)
        }
    }
}
