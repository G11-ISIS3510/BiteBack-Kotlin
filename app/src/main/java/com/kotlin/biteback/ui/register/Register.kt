package com.kotlin.biteback.ui.register

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.R
import com.kotlin.biteback.data.repositories.AuthRepository
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.kotlin.biteback.utils.NetworkUtils


@Composable
fun Register(navController: NavController, context: Context) {
    val authRepository = remember { AuthRepository() }
    val viewModel: RegisterViewModel = viewModel(factory = RegisterViewModelFactory(authRepository))
    val authState by viewModel.authState.collectAsState()
    val colors = MaterialTheme.colorScheme

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        // LOGO
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.biteback_logo),
                    contentDescription = "Biteback Logo",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Biteback",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Más baratos, más accesibles.", fontSize = 16.sp, color = colors.onBackground)

            Spacer(modifier = Modifier.height(20.dp))

            // Botones de Registro e Inicio de Sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { /* Ya estamos en Registro, no hace nada */ },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800), // Naranja cuando está activo
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, colors.onBackground),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f)
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { navController.navigate("login") }, // Ir a Login
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.background,
                        contentColor = colors.onBackground
                    ),
                    border = BorderStroke(1.dp, colors.onBackground),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f)
                ) {
                    Text("Iniciar sesión")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Línea divisoria
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), thickness = 1.dp, color = colors.onBackground)

            }

            Spacer(modifier = Modifier.height(10.dp))

            // Campos de entrada con colores corregidos
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico", color = colors.onBackground) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = LocalTextStyle.current.copy(color = colors.onBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.onBackground.copy(alpha = 0.6f),
                    cursorColor = colors.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = colors.onBackground) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(color = colors.onBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.onBackground.copy(alpha = 0.6f),
                    cursorColor = colors.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña", color = colors.onBackground) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(color = colors.onBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.onBackground.copy(alpha = 0.6f),
                    cursorColor = colors.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Estado de error
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón de registro
            Button(
                onClick = {
                    when {
                            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            errorMessage = "El correo electrónico no es válido"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Las contraseñas no coinciden"
                        }
                        password.length < 6 -> {
                            errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        }
                        !NetworkUtils.isConnected(context) -> {
                            errorMessage = "Se requiere conexión a Internet para registrarse"
                        }
                        else -> {
                            errorMessage = "" // limpia error si todo está bien
                            viewModel.registerWithEmail(email, password, confirmPassword, context, navController)
                        }
                    }
                }
                ,
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(8.dp)
            ) {
                Text("Registrarse →", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

