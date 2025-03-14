package com.kotlin.biteback.ui.productDetail

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.R
import com.kotlin.biteback.ui.components.BackButton

@Composable
fun ProductDetailScreen(navController: NavController, viewModel: ProductDetailViewModel = viewModel()) {
    val product by viewModel.product.collectAsState()
    var quantity by remember { mutableStateOf(1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            ProductImageWithBlur(imageRes = product.imageRes)

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${product.price.toInt()}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFFFA000),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$${product.oldPrice.toInt()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "Cantidad:")
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { if (quantity > 1) quantity-- }) {
                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = quantity.toString(), fontSize = 18.sp)
                    IconButton(onClick = { quantity++ }) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProductInfoCard(label = "Para vencer", value = product.timeLeft)
                    ProductInfoCard(label = "Ahorrados", value = "$${product.savings.toInt()}")
                    ProductInfoCard(label = "Tienda", value = product.store)
                    ProductInfoCard(label = "Distancia", value = product.distance)
                }

                Text(text = "Descripción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = product.description, style = MaterialTheme.typography.bodyMedium)

                InstructionsIngredientsSwitch()

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDEDED)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Warning, contentDescription = "Advertencia", tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Contiene grasas trans y calorías altas.", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Button(
                    onClick = { /* Acción */ },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                ) {
                    Text("Me lo merco")
                }
            }
        }

        BackButton(
            navController = navController,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ProductInfoCard(label: String, value: String) {
    Column(modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun InstructionsIngredientsSwitch() {
    var selectedOption by remember { mutableStateOf("Instrucciones") }
    val options = listOf("Instrucciones", "Ingredientes")
    val selectedIndex = options.indexOf(selectedOption)

    val animOffset by animateFloatAsState(
        targetValue = selectedIndex.toFloat(), label = ""
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFFF1F1F1))
    ) {
        val toggleWidth = maxWidth.value
        val indicatorWidth = toggleWidth / 2
        val offsetX = animOffset * indicatorWidth

        Box(
            modifier = Modifier
                .offset(x = offsetX.dp)
                .width(indicatorWidth.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(25.dp))
                .background(Color(0xFFFF9800))
        )

        Row(modifier = Modifier.fillMaxSize()) {
            options.forEach { option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            selectedOption = option
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (selectedOption == option) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProductImageWithBlur(imageRes: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .blur(30.dp),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
