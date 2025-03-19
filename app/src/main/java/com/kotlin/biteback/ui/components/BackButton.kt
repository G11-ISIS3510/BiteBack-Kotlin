package com.kotlin.biteback.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BackButton(navController: NavController, modifier: Modifier = Modifier) {
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = modifier
            .size(40.dp)
            .background(Color.White, shape = CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Volver",
            tint = Color.Black
        )
    }
}
