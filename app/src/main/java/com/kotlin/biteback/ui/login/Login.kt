package com.kotlin.biteback.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun Login(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    var isRegisterMode by remember { mutableStateOf(false) } // Controls Register vs Login view
    var isEmailMode by remember { mutableStateOf(false) } // Default to phone login
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Force White Background
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LOGO & Branding
            Text(
                "Biteback",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Más baratos, más accesibles.", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            // Register & Login Toggle Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { isRegisterMode = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRegisterMode) Color(0xFFFF9800) else Color.White,
                        contentColor = if (isRegisterMode) Color.White else Color.Black
                    ),
                    modifier = Modifier
                        .shadow(5.dp)
                        .weight(1f)
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { isRegisterMode = false },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isRegisterMode) Color(0xFFFF9800) else Color.White,
                        contentColor = if (!isRegisterMode) Color.White else Color.Black
                    ),
                    modifier = Modifier
                        .shadow(5.dp)
                        .weight(1f)
                ) {
                    Text("Iniciar sesión")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Iniciar sesión con:", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            // Social Login Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { /* Google Sign-In */ },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.weight(1f).padding(5.dp)
                ) {
                    Text("🔵 Sign in with Google")
                }

                Button(
                    onClick = { isEmailMode = !isEmailMode },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(1f).padding(5.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Correo electrónico", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.Gray)
                Text("  o  ", color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(10.dp))


            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Número de teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = { /* Register/Login Action */ },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(8.dp)
            ) {
                Text("Iniciar sesión →", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))


            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Olvidé mi contraseña. ", color = Color.Gray)
                Text(
                    "Recuperarla",
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically) // ✅ Fix alignment
                )
            }
        }
    }
}
