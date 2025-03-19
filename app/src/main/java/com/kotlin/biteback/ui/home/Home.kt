package com.kotlin.biteback.ui.home


import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.data.repository.LocationRepository
import com.kotlin.biteback.data.repository.ProductRepository
import com.kotlin.biteback.ui.components.BannerCard
import com.kotlin.biteback.ui.components.CategoryCard
import com.kotlin.biteback.ui.components.ExploreCard
import com.kotlin.biteback.ui.components.FoodCard
import com.kotlin.biteback.ui.components.NavBar
import com.kotlin.biteback.ui.components.ProductCard

import com.kotlin.biteback.ui.locationText.LocationText


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun Home(navController: NavController ,
         onNotificationClick: () -> Unit,
         searchViewModel: SearchBarViewModel = viewModel()) {

    // HomeProductsFlows
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(ProductRepository()))
    val products by viewModel.products.collectAsState()
    val context = LocalContext.current
    val locationRepository = LocationRepository(context)
    // SearchBar Flows
    val searchText by searchViewModel.searchQuery.collectAsState()
    val filteredProducts by searchViewModel.filteredProducts.collectAsState()
    LaunchedEffect(Unit) {
        searchViewModel.fetchProducts()
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()


        ) {

            Spacer(modifier = Modifier.height(0.dp))
            // Dirección y notificación
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LocationText(locationRepository)

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

            // SearchBar

            SearchBar(
                searchText = searchText,
                onSearchTextChanged = { searchViewModel.updateSearchQuery(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(text = "Resultados: ${filteredProducts.size}") // Para depuración
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                filteredProducts.forEach { product ->
                    FoodCard(
                        image = painterResource(id = com.kotlin.biteback.R.drawable.burger_product),
                        title = product.name,
                        discount = product.discount,
                        location = product.businessName,
                        price = product.price,
                        expanded = false,
                        onAddClick = { navController.navigate("productDetail/${product.id}") }
                    )
                }
            }

        }

        // Banner Section
        Spacer(modifier = Modifier.height(16.dp))
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
        var selectedCategory by remember { mutableStateOf("Postres") }
        val uniqueCategories = products.map { it.category }.distinct()
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
            uniqueCategories.forEach { category ->
                CategoryCard(
                    icon = painterResource(id = com.kotlin.biteback.R.drawable.donut), // Ajusta según la categoría
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
                .horizontalScroll(rememberScrollState()), // Permite desplazamiento horizontal
        ) {
            products.forEachIndexed { index, product ->
                ProductCard(
                    imageRes = com.kotlin.biteback.R.drawable.steak_image,
                    discount = (product.discount / 100).toFloat(),
                    title = product.name,
                    oldPrice = product.price.toInt(),
                    time = "15 minutos",
                    category = product.category,
                    onClick = { navController.navigate("productDetail/${product.id}") }
                )
            }

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
                discount = 15.3,
                location = "Puente Aranda",
                price = 30000.0,
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

                .horizontalScroll(rememberScrollState())
        ) {
            FoodCard(
                image = painterResource(id = com.kotlin.biteback.R.drawable.burger_product),            title = "Bandeja paisa",
                discount = 15.3,
                location = "Puente Aranda",
                price = 30000.0,
                expanded = false, // 🔥 Esto activa la versión alargada con el botón
                onAddClick = { /* Acción cuando se presiona el botón */ }
            )
        }

        NavBar(navController = navController, currentRoute = "home") //TODO Acomodar correctamente

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

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = { onSearchTextChanged(it) },
        placeholder = { Text("Busca productos, comidas o bebidas", color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.Gray
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .shadow(6.dp, shape = RoundedCornerShape(24.dp)),
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )

}