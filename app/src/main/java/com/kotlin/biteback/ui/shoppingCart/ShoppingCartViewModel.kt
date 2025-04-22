package com.kotlin.biteback.ui.shoppingCart

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ShoppingCartViewModel : ViewModel() {
    private val _message = MutableStateFlow("Bienvenido al Carrito")
    val message: StateFlow<String> = _message
}