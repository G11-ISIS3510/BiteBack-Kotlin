package com.kotlin.biteback.ui.home


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun Home(navController: NavController , viewModel: HomeViewModel = viewModel()) {
    val message by viewModel.message.collectAsState()
    Column {
        Text(message)
        Text("Pantalla Principal")
        Button(onClick = { navController.navigate("profile") }) {
            Text("Ir a Perfil")
        }
    }
}
