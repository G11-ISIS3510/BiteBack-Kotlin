package com.kotlin.biteback.ui.shoppingCart

import android.util.Log
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
import androidx.navigation.NavController
import com.kotlin.biteback.ui.components.CartItemCard
import com.kotlin.biteback.ui.components.NavBar
import com.kotlin.biteback.utils.DataStoreManager.removeProductFromCart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context
import com.kotlin.biteback.utils.NetworkUtils
import android.widget.Toast
import com.kotlin.biteback.data.model.Product
import com.kotlin.biteback.utils.DataStoreManager


@Composable
fun ShoppingCart(navController: NavController, shoppingViewModel: ShoppingCartViewModel ) {

    var quantityMap by remember { mutableStateOf(mutableMapOf<String, Int>()) }
    var mysteryBoxQuantityMap by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    val mercadingProducts by shoppingViewModel.mercarProducts.collectAsState()
    val mysteryBoxes by shoppingViewModel.mysteryBoxes.collectAsState()
    LaunchedEffect(Unit) {
        shoppingViewModel.fetchMercarProducts()
        shoppingViewModel.fetchMysteryBoxes()
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
                text = "Mi Carrito uwu",
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
            mysteryBoxes.forEach { box ->
                val quantity = mysteryBoxQuantityMap[box.id] ?: box.quantity

                CartItemCard(
                    imageUrl = "https://img.freepik.com/free-vector/realistic-question-box-mockup_23-2149489472.jpg", // imagen genérica
                    productName = box.name,
                    discountPercent = 0,
                    priceBefore = box.price.toInt(),
                    quantity = quantity,
                    onIncrease = {

                    },
                    onDecrease = {
                        if (quantity > 1) {
                            mysteryBoxQuantityMap = mysteryBoxQuantityMap.toMutableMap().apply {
                                this[box.id] = quantity - 1
                            }
                        }
                    },
                    onDelete = {
                        coroutineScope.launch {
                            DataStoreManager.removeMysteryBoxFromCart(context, box.id)
                        }
                    },
                    onClick = {
                        // Mostrar contenido de la caja
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

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
            val context = LocalContext.current

            PayNowButtonAnimated(
                totalPrice = run {
                    val productTotal = mercadingProducts.sumOf {
                        val quantity = quantityMap[it.id] ?: 1
                        val discountedPrice = it.price.toInt() - (it.price.toInt() * it.discount.toInt() / 100)
                        discountedPrice * quantity
                    }

                    val mysteryBoxTotal = mysteryBoxes.sumOf {
                        val quantity = mysteryBoxQuantityMap[it.id] ?: it.quantity
                        it.price.toInt() * quantity
                    }

                    productTotal + mysteryBoxTotal
                },
                context = context,
                mercadingProducts = mercadingProducts,
                quantityMap = quantityMap,
                shoppingViewModel = shoppingViewModel
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
    context: Context,
    mercadingProducts: List<Product>,
    quantityMap: Map<String, Int>,
    shoppingViewModel: ShoppingCartViewModel
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

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(2000)
            isPaymentDone = true
            delay(1500)

            coroutineScope.launch {
                if (NetworkUtils.isConnected(context)) {
                    val elapsedTime = shoppingViewModel.getElapsedCartTimeInMillis()
                    shoppingViewModel.registerPurchase(
                        products = mercadingProducts,
                        quantityMap = quantityMap,
                        elapsedTime = elapsedTime,
                        onSuccess = {
                            shoppingViewModel.clearCart()
                            Toast.makeText(context, "Compra registrada", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(context, "Error al registrar la compra", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    shoppingViewModel.savePendingPurchaseOffline(
                        context = context,
                        products = mercadingProducts,
                        quantityMap = quantityMap
                    )
                    Toast.makeText(context, "Compra guardada para enviar cuando haya conexión", Toast.LENGTH_LONG).show()
                }
            }
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
