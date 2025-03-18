package com.kotlin.biteback.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.ui.components.BannerCard
import com.kotlin.biteback.ui.components.CategoryCard
import com.kotlin.biteback.ui.components.ExploreCard
import com.kotlin.biteback.ui.components.FoodCard
import com.kotlin.biteback.ui.components.ProductCard


@Composable
fun Home(navController: NavController ,
         viewModel: HomeViewModel = viewModel<HomeViewModel>(),
         onNotificationClick: () -> Unit) {
    val message by viewModel.message.collectAsState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            // Texto de saludo
            Text(
                text = "Hola Danny",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(0.dp))

            // Dirección y notificación
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Las crucetas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                // Botón de notificaciones
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF8800))
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    // Indicador de notificación (punto rojo)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, shape = CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            SearchBar()
        }

        // Banner Section
        Spacer(modifier = Modifier.height(10.dp))
        BannerCard()

        // Explore
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            Arrangement.SpaceBetween
        ) {
            ExploreCard(
                title = "Restaurantes",
                subtitle = "Explorar más",
                actionText = "Explorar más →",
                imageRes = painterResource(id = com.kotlin.biteback.R.drawable.bowl_restaurant),
                backgroundColor = Color(0xFF03071E),
                titleColor = Color.White,
                subtitleColor = Color.LightGray,
                actionColor = Color(0xFFF77F00),
                onClick = { /* Acción cuando se presiona */ }
            )

            ExploreCard(
                title = "Supermercados",
                subtitle = "Explorar más",
                actionText = "Explorar más →",
                imageRes = painterResource(id = com.kotlin.biteback.R.drawable.green_vegetables),
                backgroundColor = Color(0xFFF2E8CF),
                titleColor = Color(0xFF386641),
                subtitleColor = Color.LightGray,
                actionColor = Color(0xFF2DC653),
                onClick = { /* Acción cuando se presiona */ }
            )
        }
        // Category Section
        Spacer(modifier = Modifier.height(10.dp))
        val categories = listOf("Donuts", "Café", "Hamburg")
        var selectedCategory by remember { mutableStateOf("Donuts") }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
                Arrangement.SpaceBetween
        ) {
            Text(
                text = "Categorias",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Ver mas →",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF77F00)
            )

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            categories.forEach { category ->
                CategoryCard(
                    icon = painterResource(id = com.kotlin.biteback.R.drawable.donut), // Cambia según la categoría
                    text = category,
                    isSelected = category == selectedCategory,
                    onClick = { selectedCategory = category }
                )
            }
        }
        //Near Products Section
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Productos cercanos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Location",
                tint = Color(0xFFF77F00),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "El san bernardo",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ){
            ProductCard(imageRes = com.kotlin.biteback.R.drawable.steak_image, // Reemplaza con tu imagen
                discount = 0.30f,
                title = "Filete with papas.",
                oldPrice = 25000,
                time = "15 minutos",
                category = "Paisas food",
                onClick = { navController.navigate("productDetail") })
        }

        // Food Recomendations
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recomendados",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Ver mas →",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF77F00)
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            FoodCard(
                image = painterResource(id = com.kotlin.biteback.R.drawable.burger_product),            title = "Bandeja paisa",
                discount = "15% DCTO",
                location = "Puente Aranda",
                price = "$30.000",
                oldPrice = "$40.000",
                expanded = false, // 🔥 Esto activa la versión alargada con el botón
                onAddClick = { /* Acción cuando se presiona el botón */ }
            )
        }


        // Food Categories
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Categorias varias",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            FoodCard(
                image = painterResource(id = com.kotlin.biteback.R.drawable.burger_product),            title = "Bandeja paisa",
                discount = "15% DCTO",
                location = "Puente Aranda",
                price = "$30.000",
                oldPrice = "$40.000",
                expanded = true, // 🔥 Esto activa la versión alargada con el botón
                onAddClick = { /* Acción cuando se presiona el botón */ }
            )
        }



//        Button(onClick = { navController.navigate("profile") }) {
//            Text("Ir a Perfil")
//        }
    }
}


@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF6F6F6))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Buscar",
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Busca productos, comidas o bebidas",
                color = Color.Gray
            )
        }
    }
}

