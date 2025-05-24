package com.kotlin.biteback.ui.shoppingCart

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.biteback.data.repository.CartRepository

class ShoppingCartViewModelFactory(
    private val application: Application,
    private val cartRepository: CartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingCartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingCartViewModel(application, cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}