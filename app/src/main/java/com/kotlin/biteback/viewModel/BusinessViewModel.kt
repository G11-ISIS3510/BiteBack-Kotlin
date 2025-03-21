package com.kotlin.biteback.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.repository.BusinessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BusinessViewModel(private val businessRepo: BusinessRepository): ViewModel() {

    private val _nearbyProducts = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val nearbyProducts = _nearbyProducts.asStateFlow()

    fun fetchNearbyProducts(maxDistanceKm: Double) {
        viewModelScope.launch {
            _nearbyProducts.value = businessRepo.getNearbyProducts(maxDistanceKm)
        }
    }

}