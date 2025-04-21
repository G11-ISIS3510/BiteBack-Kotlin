package com.kotlin.biteback.ui.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.kotlin.biteback.data.repositories.AuthRepository
import com.kotlin.biteback.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun registerWithEmail(
        email: String,
        password: String,
        confirmPassword: String,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Validaciones previas
            if (password != confirmPassword) {
                _authState.value = AuthState.Error("Las contraseñas no coinciden.")
                return@launch
            }

            if (password.length < 6) {
                _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres.")
                return@launch
            }

            if (!NetworkUtils.isConnected(context)) {
                _authState.value = AuthState.Error("Se requiere conexión a internet para registrarse.")
                return@launch
            }

            // Registro con Firebase
            val success = authRepository.registerWithEmail(email, password)
            _authState.value = if (success) {
                navController.navigate("home")
                AuthState.Success
            } else {
                AuthState.Error("Error al registrar usuario.")
            }
        }
    }
}

// Estados de autenticación (por si no están definidos ya en común con login)
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
