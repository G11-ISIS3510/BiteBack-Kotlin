package com.kotlin.biteback.ui.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel {
    private val _message = MutableStateFlow("Bienvenido a Home")
    val message: StateFlow<String> = _message
}