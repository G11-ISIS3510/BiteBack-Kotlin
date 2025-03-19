package com.kotlin.biteback.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NavBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        NavItem(label = "Home", route = "home", icon = Icons.Filled.Home, outlinedIcon = Icons.Outlined.Home),
        NavItem(label = "Mystery", route = "mystery", icon = Icons.Filled.Favorite, outlinedIcon = Icons.Outlined.Favorite),
        NavItem(label = "Carrito", route = "cart", icon = Icons.Filled.ShoppingCart, outlinedIcon = Icons.Outlined.ShoppingCart),
        NavItem(label = "Profile", route = "profile", icon = Icons.Filled.Person, outlinedIcon = Icons.Outlined.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF1F1F1)), // Fondo gris claro
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                val isSelected = item.route == currentRoute
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { if (!isSelected) navController.navigate(item.route) }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSelected) item.icon else item.outlinedIcon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFFFF9800) else Color.Gray
                    )
                    Text(
                        text = item.label,
                        color = if (isSelected) Color(0xFFFF9800) else Color.Gray
                    )
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
