package com.kotlin.biteback.ui.shoppingCart

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.ui.components.CartItemCard
import com.kotlin.biteback.ui.components.NavBar
import com.kotlin.biteback.utils.DataStoreManager.removeProductFromCart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ShoppingCart(navController: NavController, shoppingViewModel: ShoppingCartViewModel = viewModel()) {

    var quantityMap by remember { mutableStateOf(mutableMapOf<String, Int>()) }
    val mercadingProducts by shoppingViewModel.mercarProducts.collectAsState()

    LaunchedEffect(Unit) {
        shoppingViewModel.fetchMercarProducts()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 100.dp)
        ) {
            Text(
                text = "Mis carrito uwu",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 24.dp),
                textAlign = TextAlign.Center
            )
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            mercadingProducts.forEach { product ->
                val quantity = quantityMap[product.id] ?: 1
                val priceBefore = product.price.toInt()
                val discountPercent = product.discount.toInt()

                CartItemCard(
                    imageUrl = product.image,
                    productName = product.name,
                    discountPercent = discountPercent,
                    priceBefore = priceBefore,
                    quantity = quantity,
                    onIncrease = {
                        quantityMap = quantityMap.toMutableMap().apply {
                            this[product.id] = quantity + 1
                        }
                    },
                    onDecrease = {
                        if (quantity > 1) {
                            quantityMap = quantityMap.toMutableMap().apply {
                                this[product.id] = quantity - 1
                            }
                        }
                    },
                    onDelete = {
                        coroutineScope.launch {
                            removeProductFromCart(context, product.id)
                        }
                    },
                    onClick = {
                        // TODO: navegar al detalle del producto
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            PayNowButtonAnimated(
                totalPrice = mercadingProducts.sumOf {
                    val quantity = quantityMap[it.id] ?: 1
                    val discountedPrice = it.price.toInt() - (it.price.toInt() * it.discount.toInt() / 100)
                    discountedPrice * quantity
                },
                onPaymentConfirmed = {
                    // Mock payment logic
                }
            )
        }

        // NavBar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            NavBar(navController = navController, currentRoute = "cart")
        }
    }
}


@Composable
fun PayNowButtonAnimated(
    totalPrice: Int,
    onPaymentConfirmed: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var isPaymentDone by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = isPaymentDone, label = "PaymentTransition")

    val buttonWidth by transition.animateDp(
        transitionSpec = { tween(durationMillis = 600) }, label = "WidthAnim"
    ) { done -> if (done) 220.dp else if (isProcessing) 60.dp else 320.dp }

    val buttonColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = 600) }, label = "ColorAnim"
    ) { done -> if (done) Color(0xFF4CAF50) else Color(0xFFFF9800) }

    val cornerRadius by transition.animateDp(
        transitionSpec = { tween(durationMillis = 600) }, label = "CornerRadiusAnim"
    ) { done -> if (done) 16.dp else 50.dp }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(2000)
            isPaymentDone = true
            delay(1500)
            onPaymentConfirmed()
        }
    }

    Button(
        onClick = { isProcessing = true },
        modifier = Modifier
            .height(56.dp)
            .width(buttonWidth),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(cornerRadius),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        when {
            isPaymentDone -> {
                Icon(Icons.Default.Check, contentDescription = "Hecho", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("¡Pago aprobado!", color = Color.White, fontWeight = FontWeight.Bold)
            }
            isProcessing -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
            else -> {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pagar ahora", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("$${totalPrice}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}