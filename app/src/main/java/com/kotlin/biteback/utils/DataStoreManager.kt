package com.kotlin.biteback.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kotlin.biteback.data.model.Product
import com.kotlin.biteback.data.model.ProductWithBusiness
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object DataStoreManager  {
    private const val PREFERENCES_NAME = "search_preferences"
    private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)
    // Key for search bar
    private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
    // Key for recent products
    private val RECENT_PRODUCTS_KEY = stringPreferencesKey("recent_products")
    // Key for recent products
    private val MERCAR_PRODUCTS_KEY = stringPreferencesKey("mercar_products")


    suspend fun saveSearchQuery(context: Context, query: String) {
        context.dataStore.edit { prefs ->
            prefs[SEARCH_QUERY_KEY] = query
        }
    }

    fun getSearchQuery(context: Context): Flow<String> {
        return context.dataStore.data
            .map { prefs -> prefs[SEARCH_QUERY_KEY] ?: "" }
    }


    suspend fun addRecentProduct(context: Context, product: ProductWithBusiness) {
        context.dataStore.edit { prefs ->
            val currentList = prefs[RECENT_PRODUCTS_KEY]?.let {
                Json.decodeFromString<List<ProductWithBusiness>>(it)
            } ?: emptyList()

            val updatedList = listOf(product) + currentList.filter { it.id != product.id }
            val limitedList = updatedList.take(5)

            prefs[RECENT_PRODUCTS_KEY] = Json.encodeToString(limitedList)
        }
    }

    fun getRecentProducts(context: Context): Flow<List<ProductWithBusiness>> {
        return context.dataStore.data.map { prefs ->
            prefs[RECENT_PRODUCTS_KEY]?.let {
                Json.decodeFromString(it)
            } ?: emptyList()
        }
    }


    suspend fun mercarProduct(context: Context, product: Product) {
        context.dataStore.edit { prefs ->
            val currentList = prefs[MERCAR_PRODUCTS_KEY]?.let {
                Json.decodeFromString<List<Product>>(it)
            } ?: emptyList()

            val updatedList = listOf(product) + currentList.filter { it.id != product.id }
            val limitedList = updatedList.take(5)

            prefs[MERCAR_PRODUCTS_KEY] = Json.encodeToString(limitedList)
        }
    }

    fun getMercadosProducts(context: Context): Flow<List<Product>> {
        return context.dataStore.data.map { prefs ->
            prefs[MERCAR_PRODUCTS_KEY]?.let {
                Json.decodeFromString(it)
            } ?: emptyList()
        }
    }


}