package com.kotlin.biteback.ui.shoppingCart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.PendingPurchase
import com.kotlin.biteback.data.model.Product
import com.kotlin.biteback.data.repository.ShoppingCartRepository
import com.kotlin.biteback.utils.DataStoreManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.kotlin.biteback.data.model.MysteryCart
import com.kotlin.biteback.data.model.ProductWithBusiness
import kotlinx.coroutines.withContext
import java.util.UUID
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kotlin.biteback.data.repository.CartRepository

class ShoppingCartViewModel(application: Application, private val cartRepository: CartRepository) : AndroidViewModel(application) {


    private val purchaseRepository = ShoppingCartRepository()
    // RecentProducts
    private val _mercarProducts = MutableStateFlow<List<Product>>(emptyList())
    val mercarProducts: StateFlow<List<Product>> = _mercarProducts

    // Purcharse time
    private var purchaseStartTime: Long? = null
    private var timeoutJob: Job? = null

    private val _cartItems = MutableStateFlow<List<MysteryCart>>(emptyList())
    val cartItems: StateFlow<List<MysteryCart>> = _cartItems
    // MysteryProducts
    private val _mysteryBoxes = MutableStateFlow<List<MysteryCart>>(emptyList())
    val mysteryBoxes: StateFlow<List<MysteryCart>> = _mysteryBoxes




    fun fetchMercarProducts() {
        viewModelScope.launch {
            DataStoreManager.getMercadosProducts(getApplication()).collect {
                _mercarProducts.value = it
            }
        }
    }
    fun fetchMysteryBoxes() {
        viewModelScope.launch {
            DataStoreManager.getMysteryBoxes(getApplication()).collect {
                _mysteryBoxes.value = it
            }
        }
    }

    fun registerPurchase(
        products: List<Product>,
        quantityMap: Map<String, Int>,
        elapsedTime: Long?, // Puede ser null
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        purchaseRepository.registerPurchase(
            products = products,
            quantityMap = quantityMap,
            elapsedTime = elapsedTime,
            onSuccess = {
                clearCartTimer()
                onSuccess()
            },
            onError = onError
        )
    }

    fun clearCart() {
        viewModelScope.launch {
            DataStoreManager.clearMercarProducts(getApplication())
            DataStoreManager.clearMysteryCart(getApplication())
        }
    }



    fun markCartStarted(products: List<Product>, quantityMap: Map<String, Int>) {
        purchaseStartTime = System.currentTimeMillis()

        timeoutJob?.cancel()
        timeoutJob = viewModelScope.launch {
            delay(1 * 60 * 1000) // 5 minutos
            val elapsedTime = System.currentTimeMillis() - (purchaseStartTime ?: System.currentTimeMillis())

            purchaseRepository.registerPurchase(
                products = products,
                quantityMap = quantityMap,
                elapsedTime = null,
                onSuccess = {
                    clearCart()
                    Log.d("CartTimer", "Compra registrada automáticamente por timeout")
                },
                onError = {
                    Log.e("CartTimer", "Error registrando compra por timeout", it)
                }
            )
            purchaseStartTime = null
        }
    }



    fun getElapsedCartTimeInMillis(): Long? {
        return purchaseStartTime?.let { System.currentTimeMillis() - it }
    }

    fun clearCartTimer() {
        timeoutJob?.cancel()
        purchaseStartTime = null
    }

    fun savePendingPurchaseOffline(
        context: Context,
        products: List<Product>,
        quantityMap: Map<String, Int>
    ) {
        val pendingPurchase = PendingPurchase(
            products = products,
            quantities = quantityMap,
            timestamp = System.currentTimeMillis()
        )

        val json = Gson().toJson(pendingPurchase)
        val sharedPref = context.getSharedPreferences("offline_purchases", Context.MODE_PRIVATE)
        sharedPref.edit().putString("pending_purchase", json).apply()
    }

    fun trySendPendingPurchase(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("ThreadCheck", "Running on thread: ${Thread.currentThread().name}")
                val sharedPref = context.getSharedPreferences("offline_purchases", Context.MODE_PRIVATE)
                val json = sharedPref.getString("pending_purchase", null) ?: return@withContext
                val pending = Gson().fromJson(json, PendingPurchase::class.java)

                registerPurchase(
                    products = pending.products,
                    quantityMap = pending.quantities,
                    elapsedTime = null,
                    onSuccess = {
                        viewModelScope.launch(Dispatchers.Main) {
                            Log.d(
                                "ThreadCheck",
                                "Success callback on thread: ${Thread.currentThread().name}"
                            )
                            clearCart()
                            sharedPref.edit().remove("pending_purchase").apply()
                            Log.d("OfflinePurchase", "Compra pendiente enviada correctamente")
                        }
                    },
                    onError = {
                        Log.e("OfflinePurchase", "Falló reintento de compra pendiente", it)
                    }
                )
            }
        }
    }

    fun addRecentBox(context: Context, box: MysteryCart) {
        viewModelScope.launch {
            cartRepository.addToRecent(box)
            DataStoreManager.saveRecentMysteryBox(context, box)
        }
    }



    fun addMysteryBoxToCart(
        context: Context,
        name: String,
        price: Double,
        quantity: Int,
        contents: List<ProductWithBusiness>
    ) {
        val item = MysteryCart(
            id = UUID.randomUUID().toString(),
            name = name,
            price = price,
            quantity = quantity,
            isMysteryBox = true,
            contents = contents
        )
        // DEBUG:
        Log.d("MysteryBox", "Productos seleccionados en caja misteriosa:")
        contents.forEach { product ->
            Log.d("MysteryBox", "→ ${product.name} (ID: ${product.id})")
        }
        viewModelScope.launch {
            DataStoreManager.addMysteryBoxToCart(context, item)
            addRecentBox(context, item)
            fetchMysteryBoxes()
        }
    }

}