package com.kotlin.biteback.ui.restaurantReviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.kotlin.biteback.data.model.RestaurantRating
import com.kotlin.biteback.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RestaurantReviewsViewModel : ViewModel() {

    private val repository = RestaurantRepository()

    private val _restaurantRatings = MutableStateFlow<List<RestaurantRating>>(emptyList())
    val restaurantRatings: StateFlow<List<RestaurantRating>> = _restaurantRatings

    fun fetchWeeklyRatings() {
        viewModelScope.launch {
            _restaurantRatings.value = repository.getWeeklyAverageRatings()
        }
    }
}