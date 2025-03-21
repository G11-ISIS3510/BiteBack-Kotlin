package com.kotlin.biteback.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kotlin.biteback.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _message = MutableStateFlow("Bienvenido a Home")
    val message: StateFlow<String> = _message

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val success = authRepository.loginWithEmail(email, password)
            _authState.value = if (success) AuthState.Success else AuthState.Error("Credenciales incorrectas")
        }
    }

    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val success = authRepository.registerWithEmail(email, password)
            _authState.value = if (success) AuthState.Success else AuthState.Error("Error al registrar usuario")
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount, navController: NavController) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val success = authRepository.signInWithGoogle(account)
            _authState.value = if (success) {
                navController.navigate("home")
                AuthState.Success
            } else {
                AuthState.Error("Error al iniciar sesión con Google")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
    }
}

// Estados de autenticación
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
