package com.kotlin.biteback.ui.productDetail

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.kotlin.biteback.ui.components.BusinessMap
import com.kotlin.biteback.ui.shoppingCart.ShoppingCartViewModel
import com.kotlin.biteback.utils.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.kotlin.biteback.ui.theme.GreenAccent

@Composable
fun ProductDetailScreen(navController: NavController, productId: String, shoppingCartViewModel: ShoppingCartViewModel) {
    val factory = ProductDetailViewModelFactory()
    val viewModel: ProductDetailViewModel = viewModel(factory = factory)
    val product by viewModel.product.collectAsState()
    var quantity by remember { mutableStateOf(1) }
    val colors = MaterialTheme.colorScheme

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val business by viewModel.business.collectAsState()

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
                            text = "$${it.price.toInt()}",
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
                        ProductInfoCard(label = "Tienda", value = business?.name ?: "Cargando...")
                    }

                    Text(text = "Descripción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = it.description, style = MaterialTheme.typography.bodyMedium)



                    InstructionsMapSwitch(
                        businessLat = business?.latitude,
                        businessLng = business?.longitude
                    )




                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.navigate("cart") },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Ir al carrito",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        AddToCartButtonAnimated(
                            onAdded = {
                                scope.launch {
                                    product?.let {
                                        DataStoreManager.mercarProduct(context, it)
                                        shoppingCartViewModel.markCartStarted(
                                            products = listOf(it), // o tu lista actual del carrito
                                            quantityMap = mapOf(it.id to 1), // o el real map si ya lo tienes
                                        )

                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        )

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
fun InstructionsMapSwitch(businessLat: Double?, businessLng: Double?) {
    var selectedOption by remember { mutableStateOf("Instrucciones") }
    val colors = MaterialTheme.colorScheme

    Column {
        // Toggle
        val options = listOf("Instrucciones", "Mapa")
        val selectedIndex = options.indexOf(selectedOption)
        val animOffset by animateFloatAsState(
            targetValue = selectedIndex.toFloat(),
            label = ""
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

        Spacer(modifier = Modifier.height(8.dp))

        // Content
        if (selectedOption == "Instrucciones") {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = "Advertencia", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Contiene grasas trans y calorías altas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onBackground
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 16.dp)
            ) {
                if (businessLat != null && businessLng != null) {
                    BusinessMap(
                        businessLat = businessLat,
                        businessLng = businessLng,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Ubicación no disponible", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}



@Composable
fun AddToCartButtonAnimated(
    onAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isProcessing by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = isDone, label = "AddToCartTransition")

    val buttonWidth by transition.animateDp(
        transitionSpec = { tween(durationMillis = 600) },
        label = "WidthAnim"
    ) { done -> if (done) 220.dp else if (isProcessing) 60.dp else 320.dp }

    val buttonColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = 600) },
        label = "ColorAnim"
    ) { done ->
        if (done) GreenAccent else MaterialTheme.colorScheme.primary
    }

    val cornerRadius by transition.animateDp(
        transitionSpec = { tween(durationMillis = 600) },
        label = "CornerAnim"
    ) { done -> if (done) 16.dp else 50.dp }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(1500)
            isDone = true
            delay(1200)
            onAdded()
            isProcessing = false
            isDone = false
        }
    }

    Button(
        onClick = { isProcessing = true },
        modifier = modifier
            .height(56.dp)
            .width(buttonWidth)
            .shadow(8.dp, shape = RoundedCornerShape(cornerRadius)),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(cornerRadius),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        when {
            isDone -> {
                Icon(Icons.Default.Check, contentDescription = "Añadido", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("¡Añadido!", color = Color.White, fontWeight = FontWeight.Bold)
            }

            isProcessing -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }

            else -> {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Me lo merco", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Me lo merco →", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}


