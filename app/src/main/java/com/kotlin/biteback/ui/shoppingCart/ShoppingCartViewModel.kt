package com.kotlin.biteback.ui.shoppingCart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.PendingPurchase
import com.kotlin.biteback.data.model.Product
import com.kotlin.biteback.data.model.ProductWithBusiness
import com.kotlin.biteback.data.repository.ShoppingCartRepository
import com.kotlin.biteback.utils.DataStoreManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import com.google.gson.Gson

class ShoppingCartViewModel(application: Application) : AndroidViewModel(application) {


    private val purchaseRepository = ShoppingCartRepository()
    // RecentProducts
    private val _mercarProducts = MutableStateFlow<List<Product>>(emptyList())
    val mercarProducts: StateFlow<List<Product>> = _mercarProducts

    // Purcharse time
    private var purchaseStartTime: Long? = null
    private var timeoutJob: Job? = null


    fun fetchMercarProducts() {
        viewModelScope.launch {
            DataStoreManager.getMercadosProducts(getApplication()).collect {
                _mercarProducts.value = it
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
                clearCartTimer() // Limpia el timer si el pago fue exitoso
                onSuccess()
            },
            onError = onError
        )
    }

    fun clearCart() {
        viewModelScope.launch {
            DataStoreManager.clearMercarProducts(getApplication())
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
        val sharedPref = context.getSharedPreferences("offline_purchases", Context.MODE_PRIVATE)
        val json = sharedPref.getString("pending_purchase", null) ?: return

        val gson = Gson()
        val pending = gson.fromJson(json, PendingPurchase::class.java)

        registerPurchase(
            products = pending.products,
            quantityMap = pending.quantities,
            elapsedTime = null,
            onSuccess = {
                clearCart()
                sharedPref.edit().remove("pending_purchase").apply()
                Log.d("OfflinePurchase", "Compra pendiente enviada correctamente")
            },
            onError = {
                Log.e("OfflinePurchase", "Falló reintento de compra pendiente", it)
            }
        )
    }

}