package com.kotlin.biteback.ui.home


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kotlin.biteback.data.repository.LocationRepository
import com.kotlin.biteback.data.repository.ProductRepository
import com.kotlin.biteback.ui.components.BannerCard
import com.kotlin.biteback.ui.components.CategoryCard
import com.kotlin.biteback.ui.components.ExploreCard
import com.kotlin.biteback.ui.components.FoodCard
import com.kotlin.biteback.ui.components.NavBar
import com.kotlin.biteback.ui.components.ProductCard
import com.kotlin.biteback.ui.locationText.LocationText
import com.kotlin.biteback.viewModel.BusinessViewModel
import com.kotlin.biteback.viewModel.BusinessViewModelFactory
import com.kotlin.biteback.data.repository.BusinessRepository
import androidx.compose.material.icons.outlined.ExitToApp


import com.kotlin.biteback.utils.DataStoreManager
import kotlinx.coroutines.launch
import java.util.Locale


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun Home(navController: NavController ,
         onNotificationClick: () -> Unit,
         searchViewModel: SearchBarViewModel = viewModel())
{

    // HomeProductsFlows
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(ProductRepository()))
    val products by viewModel.products.collectAsState()
    val context = LocalContext.current
    val locationRepository = LocationRepository(context)
    // SearchBar Flows
    val searchText by searchViewModel.searchQuery.collectAsState()
    val filteredProducts by searchViewModel.filteredProducts.collectAsState()
    // Recent viewed flow
    val recentProducts by searchViewModel.recentProducts.collectAsState()

    // BusinessFLow
    val businessViewModel: BusinessViewModel = viewModel(factory= BusinessViewModelFactory(BusinessRepository(locationRepository)))
    val nearProducts by businessViewModel.nearbyProducts.collectAsState()

    LaunchedEffect(Unit) {
        searchViewModel.fetchProducts()
        businessViewModel.fetchNearbyProducts(100.0)
        searchViewModel.fetchRecentProducts()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 100.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()


            ) {

                Spacer(modifier = Modifier.height(0.dp))
                // Dirección y notificación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LocationText(locationRepository)

                    Spacer(modifier = Modifier.weight(1f))

                    // Botón de notificaciones
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8800))
                            .clickable { onNotificationClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )

                        // Indicador de notificación (punto rojo)
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Red, shape = CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                RequestAudioPermission(context)
                // SearchBar
                SearchBar(
                    searchText = searchText,
                    onSearchTextChanged = { searchViewModel.updateSearchQuery(it) },
                    onVoiceInput = { startVoiceRecognition(context, searchViewModel) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Productos encontrados
                Column {
                    Text(text = "Productos encontrados: ${filteredProducts.size}") // Para depuración
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    filteredProducts.forEach { product ->
                        FoodCard(
                            image = product.image,
                            title = product.name,
                            discount = product.discount,
                            location = product.businessName,
                            price = product.price,
                            expanded = false,
                            onAddClick = {
                                searchViewModel.onProductClicked(product)

                                val productId = product.id
                                navController.navigate("productDetail/$productId")
                            }
                        )
                    }
                }

            }

            // Banner Section
            Spacer(modifier = Modifier.height(16.dp))
            BannerCard()

            // Explore
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                ExploreCard(
                    title = "Restaurantes",
                    subtitle = "Explorar más",
                    actionText = "Explorar más →",
                    imageRes = painterResource(id = com.kotlin.biteback.R.drawable.bowl_restaurant),
                    backgroundColor = Color(0xFF03071E),
                    titleColor = Color.White,
                    subtitleColor = Color.LightGray,
                    actionColor = Color(0xFFF77F00),
                    onClick = { navController.navigate("restaurantReviews") }
                )

                ExploreCard(
                    title = "Supermercados",
                    subtitle = "Explorar más",
                    actionText = "Explorar más →",
                    imageRes = painterResource(id = com.kotlin.biteback.R.drawable.green_vegetables),
                    backgroundColor = Color(0xFFF2E8CF),
                    titleColor = Color(0xFF386641),
                    subtitleColor = Color.LightGray,
                    actionColor = Color(0xFF2DC653),
                    onClick = { /* Acción cuando se presiona */ }
                )
            }
            // Category Section
            Spacer(modifier = Modifier.height(10.dp))
            var selectedCategory by remember { mutableStateOf("Postres") }
            val uniqueCategories = products.map { it.category }.distinct()
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Categorias",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Ver mas →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF77F00)
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                uniqueCategories.forEach { category ->
                    CategoryCard(
                        icon = painterResource(id = com.kotlin.biteback.R.drawable.donut), // Ajusta según la categoría
                        text = category,
                        isSelected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                }
            }
            //Near Products Section
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Productos cercanos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFFF77F00),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Bogotá, Colombia",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                nearProducts.forEach { business ->
                    val productsList = business["filteredProducts"] as? List<Map<String, Any>> ?: emptyList()
                    productsList.forEach { product ->
                        ProductCard(
                            imageRes = (product["image"] as? String) ?: "https://imgix.ranker.com/user_node_img/50105/1002095730/original/1002095730-photo-u1?auto=format&q=60&fit=crop&fm=pjpg&dpr=2&w=355",
                            discount = ((product["discount"] as? Number ?: (0.0 / 100))).toFloat(),
                            title = product["name"] as? String ?: "Producto sin nombre",
                            oldPrice = (product["price"] as? Number)?.toInt() ?: 0,
                            time = "15 minutos",
                            category = product["category"] as? String ?: "Sin categoría",
                            onClick = {
                                val productId = product["id"] as? String ?: ""
                                navController.navigate("productDetail/$productId")
                            }
                        )
                    }
                }
            }

            // Food Recomendations
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Explorados recientemente ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Ver mas →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF77F00)
                )

            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                recentProducts.forEach { product ->
                    FoodCard(
                        image = product.image,
                        title = product.name,
                        discount = product.discount,
                        location = product.businessName,
                        price = product.price,
                        expanded = false,
                        onAddClick = {
                            navController.navigate("productDetail/${product.id}")
                        }
                    )
                }
            }


            // Food Categories
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Categorias varias",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier

                    .horizontalScroll(rememberScrollState())
            ) {
                filteredProducts.shuffled().take(3).forEach { product ->
                    FoodCard(
                        image = product.image,
                        title = product.name,
                        discount = product.discount,
                        location = product.businessName,
                        price = product.price,
                        expanded = false,
                        onAddClick = {
                            searchViewModel.onProductClicked(product)

                            navController.navigate("productDetail/${product.id}")
                        }
                    )
                }
            }

        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            NavBar(navController = navController, currentRoute = "home")
        }
    }
}


@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onVoiceInput: () -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = { onSearchTextChanged(it) },
        placeholder = { Text("Busca productos:", color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            IconButton(onClick = { onVoiceInput() }) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Entrada de voz",
                    tint = Color.Gray
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .shadow(6.dp, shape = RoundedCornerShape(24.dp)),
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

// Speach Miro Recognizer
private fun startVoiceRecognition(context: Context, searchViewModel: SearchBarViewModel) {
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { spokenText ->
                    searchViewModel.updateSearchQueryFromVoice(spokenText)
                }
            }
            override fun onError(error: Int) {
                Log.e("VoiceSearch", "Error en reconocimiento de voz: $error")
            }
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }
    val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.firstOrNull()?.let { spokenText ->
                searchViewModel.updateSearchQueryFromVoice(spokenText) //
            }
        }

        override fun onError(error: Int) {
            Log.e("VoiceSearch", "Error en reconocimiento de voz: $error")
        }

        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    speechRecognizer.startListening(recognizerIntent)
}

@Composable
fun RequestAudioPermission(context: Context) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}