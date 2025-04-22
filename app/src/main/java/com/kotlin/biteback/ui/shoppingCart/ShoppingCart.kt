package com.kotlin.biteback.ui.shoppingCart

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.ui.components.QuantityButton

@Composable
fun ShoppingCart(navController: NavController , shoppingViewModel: ShoppingCartViewModel = viewModel()) {
    val message by shoppingViewModel.message.collectAsState()

    // Quantity of product
    var quantity by remember { mutableStateOf(1) }
    // Main column page
    Column {
        Text(message)
        Text("Carritoo")
        QuantityButton(
            quantity = quantity,
            onIncrease = { quantity++ },
            onDecrease = { if (quantity > 0) quantity-- }
        )



    }


}