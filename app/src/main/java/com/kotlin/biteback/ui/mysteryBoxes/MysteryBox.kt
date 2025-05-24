package com.kotlin.biteback.ui.mysteryBoxes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.kotlin.biteback.ui.components.MysteryCard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import com.kotlin.biteback.ui.components.FoodCard
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlin.biteback.data.model.MysteryCart
import com.kotlin.biteback.ui.components.NavBar
import com.kotlin.biteback.ui.shoppingCart.ShoppingCartViewModel

@Composable
fun MysteryBox(navController: NavController, mysteryBoxViewModel : MysteryBoxViewModel, shoppingCartViewModel: ShoppingCartViewModel) {

    var quantity by remember { mutableStateOf(2) }
    val products by mysteryBoxViewModel.products.collectAsState()
    val mysteryBoxes by mysteryBoxViewModel.mysteryBoxes.collectAsState()
    val basePrice = 25000
    val totalPrice = basePrice * quantity

    // Llama a fetchProducts cuando esta pantalla se muestre por primera vez
    LaunchedEffect(Unit) {
        mysteryBoxViewModel.fetchProducts()
        mysteryBoxViewModel.loadRecentMysteryBoxes()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 100.dp)
        ) {
            MysteryBoxTopBar(
                onBackClick = {
                    navController.navigate("home") {
                        popUpTo("mystery") { inclusive = true }
                    }
                }
            )

            val context = LocalContext.current
            MysteryCard(
                imageRes = com.kotlin.biteback.R.drawable.mystery_image,
                title = "Cajita misteriosa (?)",
                price = totalPrice,
                quantity = quantity,
                onIncrease = { quantity++ },
                onDecrease = { if (quantity > 1) quantity-- },
                availableProducts = products,
                onBuyClick = { selectedQuantity ->
                    val randomProducts = products.shuffled().take(selectedQuantity)

                    shoppingCartViewModel.addMysteryBoxToCart(
                        context = context,
                        name = "Cajita misteriosa",
                        price = basePrice.toDouble(),
                        quantity = selectedQuantity,
                        contents = randomProducts
                    )
                }
            )

            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Productos posibles  ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    FoodCard(
                        image = product.image,
                        title = product.name,
                        discount = product.discount,
                        location = product.businessName,
                        price = product.price,
                        expanded = false,
                        onAddClick = {
                            navController.navigate("productDetail/${product.id}")
                        }
                    )
                }
            }

            //
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cajitas recientemente compradas ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mysteryBoxes) { mysteryBox ->
                    FoodCard(
                        image = mysteryBox.contents.firstOrNull()?.image ?: "",  // Usamos la imagen del primer producto si existe
                        title = mysteryBox.name,
                        discount = 0.0,  // Las cajas misteriosas no tienen descuento, por lo que pasamos null
                        location = "",  // No tenemos ubicación en MysteryCart, lo dejamos vacío
                        price = mysteryBox.price,
                        expanded = false,  // Aquí podrías agregar la lógica si es necesario expandir la caja misteriosa
                        onAddClick = {
                            // Lógica para agregar la MysteryBox al carrito

                        }
                    )
                }
            }

        }





        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            NavBar(navController = navController, currentRoute = "mystery")
        }
    }
}


@Composable
fun MysteryBoxTopBar(
    title: String = "Caja misteriosa",
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de regreso
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFF9800))
                .clickable { onBackClick() }
                ,contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
    }
}