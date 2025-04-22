package com.kotlin.biteback.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.kotlin.biteback.ui.home.Home
import com.kotlin.biteback.ui.login.Login
import com.kotlin.biteback.ui.productDetail.ProductDetailScreen
import com.kotlin.biteback.ui.register.Register
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.kotlin.biteback.ui.restaurantReviews.RestaurantReviews
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.biteback.ui.shoppingCart.ShoppingCart


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavigation(context: Context, startDestination: String) {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { Login(navController, context) }
        composable("register") { Register(navController, context) }
        composable("home") {
            Home(navController, onNotificationClick = { /* acción */ })
        }
        composable("restaurantReviews") { RestaurantReviews(navController) }
        composable("productDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(navController, productId)
        }
        composable("cart") {
            ShoppingCart(navController)
        }
    }
}

