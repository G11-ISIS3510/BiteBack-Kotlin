package com.kotlin.biteback.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.biteback.data.model.ProductWithBusiness
import com.kotlin.biteback.ui.theme.GreenAccent
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlin.biteback.data.model.Product
import com.kotlin.biteback.ui.productDetail.ProductDetailViewModel
import com.kotlin.biteback.ui.productDetail.ProductDetailViewModelFactory

@Composable
fun MysteryCard(
    imageRes: Int,
    title: String,
    price: Int,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    availableProducts: List<ProductWithBusiness>,
    onBuyClick: (Int) -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val factory = ProductDetailViewModelFactory()
    val productViewModel: ProductDetailViewModel = viewModel(factory = factory)

    Card (
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Gray,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$${price}.0",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            QuantityButton(
                quantity = quantity,
                onIncrease = onIncrease,
                onDecrease = onDecrease
            )

            Spacer(modifier = Modifier.height(16.dp))

            AddMysteryBoxButtonAnimated(
                quantity = quantity,
                availableProducts = availableProducts,
                onAdded = { onBuyClick(quantity) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AddMysteryBoxButtonAnimated(
    quantity: Int,
    availableProducts: List<ProductWithBusiness>,
    onAdded: (List<ProductWithBusiness>) -> Unit,
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
        if (done) GreenAccent else Color(0xFFFF9800)
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


            val mysteryItems = availableProducts.shuffled().take(quantity)
            onAdded(mysteryItems)

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
                Text("¡Caja lista!", color = Color.White, fontWeight = FontWeight.Bold)
            }

            isProcessing -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }

            else -> {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Caja", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Me lo merco →", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

