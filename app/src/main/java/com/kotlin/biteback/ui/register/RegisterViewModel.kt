package com.kotlin.biteback.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.kotlin.biteback.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun registerWithEmail(email: String, password: String, navController: NavController) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val success = authRepository.registerWithEmail(email, password)
            _authState.value = if (success) {
                navController.navigate("home")
                AuthState.Success
            } else {
                AuthState.Error("Error al registrar usuario")
            }
        }
    }
}

// Estados de autenticación
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
