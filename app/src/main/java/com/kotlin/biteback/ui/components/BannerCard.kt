package com.kotlin.biteback.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlin.biteback.ui.theme.NunitoSans
import androidx.compose.ui.text.SpanStyle
import androidx.compose.material3.*
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow

@Composable
fun BannerCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF03071E), Color(0xFF003049))
                )
            )
            .padding(16.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
        ) {
            // 📝 Textos a la izquierda
            Column(
                Modifier.padding(top = 13.dp)

            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append("¡Hasta un ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFF77F00))) {
                            append("70%")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append(" de descuento!")
                        }
                    },
                    fontSize = 18.sp,
                    fontFamily = NunitoSans,

                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Consigue tu comida preferida\ncon grandes descuentos.",
                    fontSize = 16.sp,
                    fontFamily = NunitoSans,
                    color = Color(0xFFFFFFFF)
                )
                Spacer(modifier = Modifier.height(5.dp))
                // Button
                Button(
                    onClick = { /* Acción del botón */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White // Fondo blanco
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 1.dp),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 25.dp)
                ) {
                    Text(
                        text = "Ver ahora →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF77F00) 
                    )
                }
            }

            // 🍽 Imagen alineada a la derecha
            Image(
                painter = painterResource(id = com.kotlin.biteback.R.drawable.spoon_food), // 📌 Cambia con tu imagen
                contentDescription = "Descuento Comida",
                contentScale = ContentScale.Crop,

                modifier = Modifier
                    .size(250.dp) // La hace más grande

            )
        }
    }
}


