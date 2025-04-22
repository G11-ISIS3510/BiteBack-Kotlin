package com.kotlin.biteback.ui.shoppingCart

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ShoppingCart(navController: NavController , viewModel: ShoppingCartViewModel = viewModel()) {
    val message by viewModel.message.collectAsState()
    Column {
        Text(message)
        Text("Carritoo")

    }
}