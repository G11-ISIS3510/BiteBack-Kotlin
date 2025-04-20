package com.kotlin.biteback.ui.login

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kotlin.biteback.R
import com.kotlin.biteback.data.repositories.AuthRepository
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun Login(navController: NavController, context: Context) {
    val authRepository = remember { AuthRepository() }
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(authRepository))
    val authState by viewModel.authState.collectAsState()
    val colors = MaterialTheme.colorScheme

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val googleSignInClient: GoogleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("685056619394-9gslsqdhe5rhsrd68fsq083enl1s0gvg.apps.googleusercontent.com")
                .requestEmail()
                .build()
        )
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            viewModel.signInWithGoogle(account, navController)
        } catch (e: ApiException) {
            println("Error en Google Sign-In: ${e.message}")
        }
    }

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
            Text("Más baratos, más accesibles.", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            // Botones de Registro e Inicio de Sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.navigate("register") },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.background,
                        contentColor = colors.onBackground
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f)
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.Gray),
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f)
                ) {
                    Text("Iniciar sesión")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Iniciar sesión con:", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            // Botones de Google y Email
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier.weight(1f).padding(5.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Google")
                }

                Button(
                    onClick = { },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(1f).padding(5.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Email", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.Gray)
                Text("  o  ", color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Campos de entrada de correo y contraseña
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
                textStyle = LocalTextStyle.current.copy(color = colors.onBackground), // ✅ Cambia el color del texto
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.onBackground.copy(alpha = 0.6f),
                    cursorColor = colors.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(20.dp))

            // Estado de autenticación
            when (authState) {
                is AuthState.Loading -> CircularProgressIndicator()
                is AuthState.Error -> Text(
                    text = (authState as AuthState.Error).message,
                    color = Color.Red
                )
                is AuthState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("home")
                    }
                }
                else -> {}
            }

            // Botón de Inicio de Sesión
            Button(
                onClick = { viewModel.loginWithEmail(email, password) },
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

            // Olvidé mi contraseña
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Olvidé mi contraseña. ", color = Color.Gray)
                Text(
                    "Recuperarla",
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}
