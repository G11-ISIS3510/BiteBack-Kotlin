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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.kotlin.biteback.ui.components.BackButton
import com.kotlin.biteback.data.repository.ProductDetailRepository
import com.kotlin.biteback.utils.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(navController: NavController, productId: String) {
    val factory = ProductDetailViewModelFactory(ProductDetailRepository())
    val viewModel: ProductDetailViewModel = viewModel(factory = factory)
    val product by viewModel.product.collectAsState()
    var quantity by remember { mutableStateOf(1) }
    val colors = MaterialTheme.colorScheme

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        viewModel.fetchProduct(productId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            product?.let {
                ProductImageWithBlur(imageUrl = it.image)
            }

            Column(modifier = Modifier.padding(16.dp)) {
                product?.let {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val discountedPrice = (it.price * (1 - (it.discount / 100))).toInt()

                        Text(
                            text = "$$discountedPrice",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$${it.price.toInt()}", // ✅ Precio original (tachado)
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
                        val formattedExpirationDate by viewModel.formattedExpirationDate.collectAsState()
                        ProductInfoCard(label = "Expira en", value = formattedExpirationDate)
                        ProductInfoCard(label = "Descuento", value = "${it.discount}%")
                        ProductInfoCard(label = "Tienda", value = it.businessId)
                    }

                    Text(text = "Descripción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = it.description, style = MaterialTheme.typography.bodyMedium)

                    InstructionsIngredientsSwitch()

                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = "Advertencia", tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Contiene grasas trans y calorías altas.", style = MaterialTheme.typography.bodySmall, color = colors.onBackground)
                        }
                    }
                    // use it as a product?
                    Button(
                        onClick = {
                            scope.launch {
                                DataStoreManager.mercarProduct(context, it)
                            }


                        }, //TODO ir a compra cuando esté listo
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text("Me lo merco")
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
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

@Composable
fun ProductImageWithBlur(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberImagePainter(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .blur(30.dp),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = rememberImagePainter(imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun InstructionsIngredientsSwitch() {
    var selectedOption by remember { mutableStateOf("Instrucciones") }
    val options = listOf("Instrucciones", "Ingredientes")
    val selectedIndex = options.indexOf(selectedOption)
    val colors = MaterialTheme.colorScheme

    val animOffset by animateFloatAsState(
        targetValue = selectedIndex.toFloat(), label = ""
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(colors.surface)
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
                .background(colors.primary)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            options.forEach { option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { selectedOption = option },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (selectedOption == option) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
