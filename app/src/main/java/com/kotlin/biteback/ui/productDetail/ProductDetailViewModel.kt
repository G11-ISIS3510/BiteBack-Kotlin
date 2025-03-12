package com.kotlin.biteback.ui.productDetail


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductDetailViewModel: ViewModel() {
    private val _message = MutableStateFlow("Bienvenido a Home")
    val message: StateFlow<String> = _message
}