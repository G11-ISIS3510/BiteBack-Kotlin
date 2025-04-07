package com.kotlin.biteback.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color

@Composable
fun NavBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        NavItem("Home", "home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("Mystery", "mystery", Icons.Filled.Favorite, Icons.Outlined.Favorite),
        NavItem("Carrito", "cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        NavItem("Profile", "profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val colors = MaterialTheme.colorScheme
    val validRoutes = listOf("home")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .background(colors.surface) // Color dinámico para fondo base
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(colors.surface.copy(alpha = 0.9f)), // Color dinámico con transparencia
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEach { item ->
                    val isSelected = item.route == currentRoute
                    val isEnabled = item.route in validRoutes
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .then(
                                if (isEnabled && !isSelected) {
                                    Modifier.clickable { navController.navigate(item.route) }
                                } else Modifier
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.icon else item.outlinedIcon,
                            contentDescription = item.label,
                            tint = if (isSelected) colors.primary else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) colors.primary else Color.Gray
                        )
                    }
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
