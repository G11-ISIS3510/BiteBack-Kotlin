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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlin.biteback.ui.restaurantReviews.RestaurantReviews
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.biteback.ui.shoppingCart.ShoppingCart
import com.kotlin.biteback.ui.shoppingCart.ShoppingCartViewModel
import com.kotlin.biteback.ui.login.LoginViewModel
import com.kotlin.biteback.ui.login.LoginViewModelFactory
import com.kotlin.biteback.data.repositories.AuthRepository


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavigation(context: Context, startDestination: String) {
    val navController = rememberNavController()
    val shoppingViewModel: ShoppingCartViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(AuthRepository()))

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { Login(navController, context) }
        composable("register") { Register(navController, context) }

        composable("home") {
            Home(
                navController,
                onNotificationClick = {
                    loginViewModel.logout(context, navController)
                }
            )
        }

        composable("restaurantReviews") { RestaurantReviews(navController) }
        composable("productDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(navController, productId, shoppingCartViewModel = shoppingViewModel)
        }
        composable("cart") {
            ShoppingCart(navController,shoppingViewModel)
        }
    }
}


