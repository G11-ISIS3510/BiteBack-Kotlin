package com.kotlin.biteback.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kotlin.biteback.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import com.kotlin.biteback.utils.NetworkUtils
import com.kotlin.biteback.utils.LocalStorage
import com.kotlin.biteback.utils.FileStorage
import com.kotlin.biteback.data.local.UserDatabase
import com.kotlin.biteback.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _message = MutableStateFlow("Bienvenido a Home")
    val message: StateFlow<String> = _message

    fun loginWithEmail(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                if (NetworkUtils.isConnected(context)) {
                    println("Conectado a internet. Usando FirebaseAuth para login.")
                    val success = authRepository.loginWithEmail(email, password)
                    if (success) {
                        println("Login con Firebase exitoso.")
                        LocalStorage.saveCredentials(context, email, password)
                        _authState.value = AuthState.Success
                        FileStorage.appendEmailToFile(context, email)
                        // Guardar en Room
                        val db = UserDatabase.getDatabase(context)
                        viewModelScope.launch {
                            db.userDao().insertUser(UserEntity(email = email, password = password))
                        }

                    } else {
                        println("Firebase: Credenciales incorrectas.")
                        _authState.value = AuthState.Error("Credenciales incorrectas")
                    }
                } else {
                    println("Sin internet. Usando LocalStorage para login offline.")
                    val savedEmail = LocalStorage.getEmail(context)
                    val savedPassword = LocalStorage.getPassword(context)
                    println("Email guardado: $savedEmail | Password guardado: $savedPassword")
                    if (email == savedEmail && password == savedPassword) {
                        println("Login offline exitoso con LocalStorage.")
                        _authState.value = AuthState.Success
                    } else {
                        // Si falla con LocalStorage, intentamos con Room
                        val db = UserDatabase.getDatabase(context)
                        viewModelScope.launch {
                            val user = withContext(Dispatchers.IO) {
                                db.userDao().getUserByCredentials(email, password)
                            }
                            if (user != null) {
                                println("✅ Login offline exitoso con Room.")
                                _authState.value = AuthState.Success
                            } else {
                                println("Login offline fallido. No hay coincidencias ni en LocalStorage ni en Room.")
                                _authState.value = AuthState.Error("No hay conexión y las credenciales no coinciden")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Excepción detectada en login: ${e.message}")
                val savedEmail = LocalStorage.getEmail(context)
                val savedPassword = LocalStorage.getPassword(context)
                println("(Catch) Email guardado: $savedEmail | Password guardado: $savedPassword")
                if (email == savedEmail && password == savedPassword) {
                    println("(Catch) Login offline exitoso con LocalStorage.")
                    _authState.value = AuthState.Success
                } else {
                    println("(Catch) Login offline fallido. Credenciales no coinciden.")
                    _authState.value = AuthState.Error("No se puede iniciar sesión sin conexión a internet")
                }
            }
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

    fun logout(context: Context, navController: NavController) {
        authRepository.logout()
        LocalStorage.clearCredentials(context)
        _authState.value = AuthState.Idle
        navController.navigate("login")
    }
}

// Estados de autenticación
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
