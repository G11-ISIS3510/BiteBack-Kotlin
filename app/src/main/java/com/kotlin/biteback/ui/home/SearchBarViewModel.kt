package com.kotlin.biteback.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.ProductWithBusiness
import com.kotlin.biteback.data.repository.ProductWithBusinessRepository
import com.kotlin.biteback.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.kotlin.biteback.utils.NetworkUtils

class SearchBarViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ProductWithBusinessRepository(application.applicationContext)
    // List products
    private val _products = MutableStateFlow<List<ProductWithBusiness>>(emptyList())
    val products: StateFlow<List<ProductWithBusiness>> = _products
    // Control SearchQuery bar
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    // RecentProducts
    private val _recentProducts = MutableStateFlow<List<ProductWithBusiness>>(emptyList())
    val recentProducts: StateFlow<List<ProductWithBusiness>> = _recentProducts

    init {
        loadLastSearchQuery()
    }

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
                // OFFLINE: read data from cache
                repo.getCachedProducts().collect {
                    println("Reading cache data from search products ")
                    _products.value = it
                }
            }
        }
    }


    fun fetchRecentProducts() {
        viewModelScope.launch {
            DataStoreManager.getRecentProducts(getApplication()).collect {
                _recentProducts.value = it
            }
        }
    }

    fun onProductClicked(product: ProductWithBusiness) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            DataStoreManager.addRecentProduct(context, product)
            println("Saving clicked product")
        }
    }


    private fun loadLastSearchQuery() {
        viewModelScope.launch {
            DataStoreManager.getSearchQuery(getApplication()).collect { query ->
                _searchQuery.value = query
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            DataStoreManager.saveSearchQuery(getApplication(), query)
        }
    }

    fun updateSearchQueryFromVoice(result: String) {
        _searchQuery.value = result
        viewModelScope.launch {
            DataStoreManager.saveSearchQuery(getApplication(), result)
        }
    }
}