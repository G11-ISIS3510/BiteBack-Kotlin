package com.kotlin.biteback.ui.home

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

class SearchBarViewModel : ViewModel() {

    private val repository = ProductWithBusinessRepository()
    private val _products = MutableStateFlow<List<ProductWithBusiness>>(emptyList())
    val products: StateFlow<List<ProductWithBusiness>> = _products
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery





    // Input Reactive Flow
    val filteredProducts = combine(_searchQuery, _products) { query, productList ->
        val lowerQuery = query.lowercase()
        productList.filter {
            it.name.lowercase().contains(lowerQuery) || it.businessName.lowercase().contains(lowerQuery)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun fetchProducts() {
        viewModelScope.launch {
            val businessMap = repository.fetchBusinesses()
            val productList = repository.fetchProducts(businessMap)
            _products.value = productList
        }
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSearchQueryFromVoice(result: String) {
        _searchQuery.value = result
    }



}