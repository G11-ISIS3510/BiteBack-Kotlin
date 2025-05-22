package com.kotlin.biteback.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.util.LruCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kotlin.biteback.data.model.User
import com.kotlin.biteback.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserProfileRepository(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ✅ Reutiliza el mismo archivo de preferencias del login
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    // CACHING STRATEGY - LRU Cache para imágenes de perfil
    private val imageCache = LruCache<String, Bitmap>(4 * 1024 * 1024)

    // EVENTUAL CONNECTIVITY - Lista de cambios offline pendientes
    private val pendingChanges = mutableListOf<Map<String, Any>>()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun loadUserProfile(): User? = withContext(Dispatchers.IO) {
        _isLoading.value = true
        val userId = auth.currentUser?.uid ?: return@withContext null
        try {
            val cachedUser = loadFromLocalCache(userId)
            if (cachedUser != null) _user.value = cachedUser

            if (NetworkUtils.isConnected(context)) {
                val firebaseUser = loadFromFirebase(userId)
                if (firebaseUser != null) {
                    _user.value = firebaseUser
                    saveToLocalCache(firebaseUser)
                }
                syncPendingChanges()
            }
            _user.value
        } catch (e: Exception) {
            _user.value ?: loadFromLocalCache(userId)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun uploadProfileImage(imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: return@withContext null
            val storageRef = storage.reference.child("profile_images/$userId.jpg")
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            return@withContext downloadUrl.toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateProfile(
        name: String,
        phoneNumber: String,
        profileImageUrl: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val userId = auth.currentUser?.uid ?: return@withContext false

        val updates = mutableMapOf<String, Any>(
            "name" to name,
            "phoneNumber" to phoneNumber,
            "updatedAt" to System.currentTimeMillis()
        )
        profileImageUrl?.let { updates["profileImageUrl"] = it }

        if (NetworkUtils.isConnected(context)) {
            try {
                firestore.collection("users").document(userId)
                    .update(updates)
                    .await()
                _user.value?.let {
                    val updatedUser = it.copy(
                        name = name,
                        phoneNumber = phoneNumber,
                        profileImageUrl = profileImageUrl ?: it.profileImageUrl,
                        updatedAt = System.currentTimeMillis()
                    )
                    _user.value = updatedUser
                    saveToLocalCache(updatedUser)
                }
                return@withContext true
            } catch (e: Exception) {
                savePendingChange(updates)
                return@withContext false
            }
        } else {
            savePendingChange(updates)
            _user.value?.let {
                val updatedUser = it.copy(
                    name = name,
                    phoneNumber = phoneNumber,
                    profileImageUrl = profileImageUrl ?: it.profileImageUrl,
                    updatedAt = System.currentTimeMillis()
                )
                _user.value = updatedUser
                saveToLocalCache(updatedUser)
            }
            return@withContext true
        }
    }

    fun getCachedImage(key: String): Bitmap? = imageCache.get(key)
    fun cacheImage(key: String, bitmap: Bitmap) {
        imageCache.put(key, bitmap)
    }

    private fun loadFromLocalCache(userId: String): User? {
        return try {
            User(
                id = sharedPrefs.getString("${userId}_id", "") ?: "",
                email = sharedPrefs.getString("${userId}_email", "") ?: "",
                name = sharedPrefs.getString("${userId}_name", "") ?: "",
                phoneNumber = sharedPrefs.getString("${userId}_phone", "") ?: "",
                profileImageUrl = sharedPrefs.getString("${userId}_image", "") ?: "",
                points = sharedPrefs.getInt("${userId}_points", 0),
                createdAt = sharedPrefs.getLong("${userId}_created", 0),
                updatedAt = sharedPrefs.getLong("${userId}_updated", 0)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun saveToLocalCache(user: User) {
        with(sharedPrefs.edit()) {
            putString("${user.id}_id", user.id)
            putString("${user.id}_email", user.email)
            putString("${user.id}_name", user.name)
            putString("${user.id}_phone", user.phoneNumber)
            putString("${user.id}_image", user.profileImageUrl)
            putInt("${user.id}_points", user.points)
            putLong("${user.id}_created", user.createdAt)
            putLong("${user.id}_updated", user.updatedAt)
            apply()
        }
    }

    private suspend fun loadFromFirebase(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                User(
                    id = document.id,
                    email = document.getString("email") ?: "",
                    name = document.getString("name") ?: "",
                    phoneNumber = document.getString("phoneNumber") ?: "",
                    profileImageUrl = document.getString("profileImageUrl") ?: "",
                    points = document.getLong("points")?.toInt() ?: 0,
                    createdAt = document.getLong("createdAt") ?: 0,
                    updatedAt = document.getLong("updatedAt") ?: 0
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun savePendingChange(updates: Map<String, Any>) {
        pendingChanges.add(updates)
        sharedPrefs.edit().putString("pending_changes", pendingChanges.toString()).apply()
    }

    private suspend fun syncPendingChanges() {
        if (pendingChanges.isEmpty()) return
        val userId = auth.currentUser?.uid ?: return
        try {
            for (change in pendingChanges.toList()) {
                firestore.collection("users").document(userId)
                    .update(change)
                    .await()
            }
            pendingChanges.clear()
            sharedPrefs.edit().remove("pending_changes").apply()
        } catch (e: Exception) {
            // mantener cambios si fallan
        }
    }
}
