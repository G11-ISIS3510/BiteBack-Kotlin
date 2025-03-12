package com.kotlin.biteback.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.biteback.ui.theme.RedAccent


@Composable
fun ProductCard(
    imageRes: Int,
    discount: Float,
    title: String,
    oldPrice: Int,
    time: String,
    category: String
) {
    var discountFormat: Int = (discount * 100).toInt()
    var actualPrice: Int = (oldPrice * discount).toInt()
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.height(250.dp).width(220.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .background(RedAccent)
                        .padding(5.dp)
                ) {
                    Text(
                        text = "$discountFormat% off",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row {
                    Text(
                        text = "$$actualPrice",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$$oldPrice",
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Text(text = "⏳ $time", fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "🍽️ $category", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}