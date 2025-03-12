package com.kotlin.biteback.ui.login


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel:  ViewModel() {
    private val _message = MutableStateFlow("Bienvenido a Home")
    val message: StateFlow<String> = _message
}