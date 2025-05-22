package com.kotlin.biteback.ui.mysteryBoxes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.ProductWithBusiness
import com.kotlin.biteback.data.repository.ProductWithBusinessRepository
import com.kotlin.biteback.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MysteryBoxViewModel(application: Application) :  AndroidViewModel(application) {
    private val _message = MutableStateFlow("Bienvenido a Home")
    val message: StateFlow<String> = _message

    private val repo = ProductWithBusinessRepository(application.applicationContext)
    private val _products = MutableStateFlow<List<ProductWithBusiness>>(emptyList())
    val products: StateFlow<List<ProductWithBusiness>> = _products


    fun fetchProducts() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            if (NetworkUtils.isConnected(context)) {
                // ONLINE: Download the firestore and reload cache
                val list = repo.fetchAndCacheProducts()
                _products.value = list
            } else {
                // OFFLINE: read data from cache
                repo.getCachedProducts().collect {
                    println("Reading cache data from search products ")
                    _products.value = it
                }
            }
        }
    }

}