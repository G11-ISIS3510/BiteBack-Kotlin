package com.kotlin.biteback.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.ProductWithBusiness
import com.kotlin.biteback.data.repository.ProductWithBusinessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.kotlin.biteback.utils.NetworkUtils

class SearchBarViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ProductWithBusinessRepository(application.applicationContext)

    private val _products = MutableStateFlow<List<ProductWithBusiness>>(emptyList())
    val products: StateFlow<List<ProductWithBusiness>> = _products
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredProducts = combine(_searchQuery, _products) { query, productList ->
        val lower = query.lowercase()
        productList.filter {
            it.name.lowercase().contains(lower) || it.businessName.lowercase().contains(lower)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun fetchProducts() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            if (NetworkUtils.isConnected(context)) {
                // ONLINE: Download the firestore and reload cache
                val list = repo.fetchAndCacheProducts()
                _products.value = list
            } else {
                // OFFLINE: read data from cahce
                repo.getCachedProducts().collect {
                    _products.value = it
                }
            }
        }
    }

    fun updateSearchQueryFromVoice(result: String) {
        _searchQuery.value = result
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}