package com.kotlin.biteback.ui.home


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.ui.components.ProductCard


@Composable
fun Home(navController: NavController , viewModel: HomeViewModel = viewModel<HomeViewModel>()) {
    val message by viewModel.message.collectAsState()

    Column {
        Text(message)
        Text("Pantalla Principal")

        ProductCard(imageRes = com.kotlin.biteback.R.drawable.steak_image, // Reemplaza con tu imagen
            discount = 0.30f,
            title = "Filete with papas.",
            oldPrice = 25000,
            time = "15 minutos",
            category = "Paisas food")

//        Button(onClick = { navController.navigate("profile") }) {
//            Text("Ir a Perfil")
//        }
    }
}
