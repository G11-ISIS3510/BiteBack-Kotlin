package com.kotlin.biteback.ui.shoppingCart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.Product
import com.kotlin.biteback.data.model.ProductWithBusiness
import com.kotlin.biteback.data.repository.ShoppingCartRepository
import com.kotlin.biteback.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingCartViewModel(application: Application) : AndroidViewModel(application) {


    private val purchaseRepository = ShoppingCartRepository()
    // RecentProducts
    private val _mercarProducts = MutableStateFlow<List<Product>>(emptyList())
    val mercarProducts: StateFlow<List<Product>> = _mercarProducts

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
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        purchaseRepository.registerPurchase(products, quantityMap, onSuccess, onError)
    }

    fun clearCart() {
        viewModelScope.launch {
            DataStoreManager.clearMercarProducts(getApplication())
        }
    }

}