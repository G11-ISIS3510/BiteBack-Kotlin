package com.kotlin.biteback.navigation


import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.kotlin.biteback.ui.home.Home
import com.kotlin.biteback.ui.login.Login


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") { Login(navController) }
        composable("home") { Home(navController) }
    }
}