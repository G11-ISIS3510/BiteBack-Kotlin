package com.kotlin.biteback.utils

import android.content.Context

object LocalStorage {
    private const val CREDENTIALS_PREF = "credentials_prefs"
    private const val USER_DATA_PREF = "user_data_prefs"

    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"

    // Credenciales (se borran al logout)
    fun saveCredentials(context: Context, email: String, password: String) {
        context.getSharedPreferences(CREDENTIALS_PREF, Context.MODE_PRIVATE).edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply()
    }

    fun getEmail(context: Context): String? {
        return context.getSharedPreferences(CREDENTIALS_PREF, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, null)
    }

    fun getPassword(context: Context): String? {
        return context.getSharedPreferences(CREDENTIALS_PREF, Context.MODE_PRIVATE)
            .getString(KEY_PASSWORD, null)
    }

    fun clearCredentials(context: Context) {
        context.getSharedPreferences(CREDENTIALS_PREF, Context.MODE_PRIVATE).edit()
            .clear()
            .apply()
    }

    // Datos del usuario (no se borran al logout)
    private fun nameKey(email: String) = "user_name_$email"
    private fun phoneKey(email: String) = "user_phone_$email"

    fun saveUserName(context: Context, email: String, name: String) {
        context.getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE).edit()
            .putString(nameKey(email), name)
            .apply()
    }

    fun getUserName(context: Context, email: String): String {
        return context.getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE)
            .getString(nameKey(email), "") ?: ""
    }

    fun saveUserPhone(context: Context, email: String, phone: String) {
        context.getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE).edit()
            .putString(phoneKey(email), phone)
            .apply()
    }

    fun getUserPhone(context: Context, email: String): String {
        return context.getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE)
            .getString(phoneKey(email), "") ?: ""
    }
}
