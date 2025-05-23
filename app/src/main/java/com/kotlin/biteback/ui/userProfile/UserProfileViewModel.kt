package com.kotlin.biteback.ui.userProfile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.biteback.data.model.User
import com.kotlin.biteback.data.repository.UserProfileRepository
import com.kotlin.biteback.utils.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileViewModel(
    private val repository: UserProfileRepository,
    private val context: Context
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage

    val userEmail = LocalStorage.getEmail(context) ?: "No disponible"

    private val _tempName = MutableStateFlow(LocalStorage.getUserName(context, userEmail))
    val tempName: StateFlow<String> = _tempName

    private val _tempPhoneNumber = MutableStateFlow(LocalStorage.getUserPhone(context, userEmail))
    val tempPhoneNumber: StateFlow<String> = _tempPhoneNumber

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userProfile = withContext(Dispatchers.IO) {
                    repository.loadUserProfile()
                }

                val (localName, localPhone) = withContext(Dispatchers.IO) {
                    val name = LocalStorage.getUserName(context, userEmail)
                    val phone = LocalStorage.getUserPhone(context, userEmail)
                    name to phone
                }

                val finalUser = userProfile?.copy(
                    name = if (localName.isNotEmpty()) localName else userProfile.name,
                    phoneNumber = if (localPhone.isNotEmpty()) localPhone else userProfile.phoneNumber
                ) ?: User(
                    name = localName,
                    phoneNumber = localPhone,
                    email = userEmail,
                    profileImageUrl = "",
                    points = 0
                )

                _user.value = finalUser
                _tempName.value = finalUser.name
                _tempPhoneNumber.value = finalUser.phoneNumber

            } catch (e: Exception) {
                _updateMessage.value = "Error al cargar perfil: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTempName(name: String) {
        _tempName.value = name
    }

    fun updateTempPhoneNumber(phoneNumber: String) {
        _tempPhoneNumber.value = phoneNumber
    }

    fun saveProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val name = _tempName.value.trim()
                val phone = _tempPhoneNumber.value.trim()

                withContext(Dispatchers.IO) {
                    LocalStorage.saveUserName(context, userEmail, name)
                    LocalStorage.saveUserPhone(context, userEmail, phone)
                }

                val success = repository.updateProfile(name = name, phoneNumber = phone)

                if (success) {
                    _user.value = _user.value?.copy(name = name, phoneNumber = phone) ?: User(
                        name = name,
                        phoneNumber = phone,
                        email = userEmail,
                        profileImageUrl = "",
                        points = 0
                    )
                    _updateMessage.value = "Perfil actualizado correctamente"
                } else {
                    _updateMessage.value = "Error al actualizar perfil en servidor, cambios guardados localmente."
                }

            } catch (e: Exception) {
                _updateMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _isUploadingImage.value = true
            try {
                val imageUrl = repository.uploadProfileImage(imageUri)

                if (!imageUrl.isNullOrEmpty()) {
                    val currentUser = _user.value
                    if (currentUser != null) {
                        val success = repository.updateProfile(
                            name = _tempName.value.trim(),
                            phoneNumber = _tempPhoneNumber.value.trim(),
                            profileImageUrl = imageUrl
                        )

                        if (success) {
                            Log.d("UserProfileViewModel", "Imagen subida correctamente: $imageUrl")

                            _user.value = currentUser.copy(profileImageUrl = imageUrl)
                            _updateMessage.value = "Imagen de perfil actualizada correctamente"
                        } else {
                            Log.e("UserProfileViewModel", "Error al actualizar el perfil en servidor")
                            _updateMessage.value = "Error al actualizar imagen en el servidor"
                        }
                    } else {
                        Log.e("UserProfileViewModel", "currentUser es null")
                        _updateMessage.value = "Error: No hay usuario cargado"
                    }
                } else {
                    Log.e("UserProfileViewModel", "imageUrl retornada es nula o vacía")
                    _updateMessage.value = "Error al subir imagen: URL inválida"
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Excepción al subir imagen: ${e.message}")
                _updateMessage.value = "Error al subir imagen: ${e.message}"
            } finally {
                _isUploadingImage.value = false
            }
        }
    }

    fun clearMessage() {
        _updateMessage.value = null
    }

    fun refreshProfile() {
        loadUserProfile()
    }
}
