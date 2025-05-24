package com.kotlin.biteback

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.kotlin.biteback.navigation.AppNavigation
import com.kotlin.biteback.ui.theme.BiteBackTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.kotlin.biteback.data.repository.CartRepository
import com.kotlin.biteback.ui.shoppingCart.ShoppingCartViewModel
import com.kotlin.biteback.ui.shoppingCart.ShoppingCartViewModelFactory


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        enableEdgeToEdge()
        setContent {
            BiteBackTheme {
                val user = FirebaseAuth.getInstance().currentUser
                val startDestination = if (user != null) "home" else "login"
                val appViewModel: AppViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {

                        NoInternetBanner(appViewModelParam = appViewModel)

                        AppNavigation(
                            context = this@MainActivity,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun NoInternetBanner(appViewModelParam: AppViewModel) {

    val isConnected by appViewModelParam.isConnected.collectAsState()
    var showBanner by remember { mutableStateOf(false) }
    var connectionRestored by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val cartRepository = remember { CartRepository(context) }
    val owner = checkNotNull(LocalViewModelStoreOwner.current)

    val shoppingViewModel: ShoppingCartViewModel = viewModel(
        modelClass = ShoppingCartViewModel::class.java,
        viewModelStoreOwner = owner,
        factory = ShoppingCartViewModelFactory(application, cartRepository)
    )


    LaunchedEffect(isConnected) {
        if (!isConnected) {
            showBanner = true
            connectionRestored = false
        } else if (showBanner) {
            connectionRestored = true
            delay(1000)

            // Intentamos enviar compra pendiente
            shoppingViewModel.trySendPendingPurchase(context)

            delay(1000)
            showBanner = false
        }
    }

    AnimatedVisibility(
        visible = showBanner,
        enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { -80 }),
        exit = fadeOut(tween(500)) + slideOutVertically(targetOffsetY = { -80 })
    ) {
        val backgroundColor by animateColorAsState(
            targetValue = if (connectionRestored) Color(0xFF4CAF50) else Color(0xFFD32F2F),
            animationSpec = tween(500)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.95f)
                    .padding(top = 16.dp)
                    .background(backgroundColor, RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (connectionRestored) Icons.Default.Check else Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (connectionRestored) "¡Conexión restaurada!" else "No hay conexión a internet",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
