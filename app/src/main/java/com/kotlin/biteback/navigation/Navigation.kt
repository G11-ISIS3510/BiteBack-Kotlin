package com.kotlin.biteback.navigation


import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.kotlin.biteback.ui.home.Home
import com.kotlin.biteback.ui.login.Login
import com.kotlin.biteback.ui.productDetail.ProductDetailScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    NavHost(navController, startDestination = "home") {
        composable("home") { Home(navController, onNotificationClick = { /* Acción al hacer clic en notificaciones */ }) }
        composable("productDetail") { ProductDetailScreen(navController)}

// TODO: Connect
//     NavHost(navController, startDestination = "login") {
//         composable("login") { Login(navController) }
//         compoable("home") { Home(navController) }


    }
}