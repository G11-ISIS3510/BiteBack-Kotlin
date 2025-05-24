package com.kotlin.biteback.data.repository

import android.content.Context
import android.util.LruCache
import com.kotlin.biteback.data.model.MysteryCart
import com.kotlin.biteback.utils.DataStoreManager

class CartRepository(private val context: Context) {

    private val recentCache = LruCache<String, MysteryCart>(5)

    suspend fun loadRecentMysteryBoxes(): List<MysteryCart> {
        val stored = DataStoreManager.getRecentMysteryBoxes(context)
        stored.forEach { recentCache.put(it.id, it) }
        return recentCache.snapshot().values.toList()
    }

    fun addToRecent(mysteryBox: MysteryCart) {
        recentCache.put(mysteryBox.id, mysteryBox)
    }

    fun getCachedRecent(): List<MysteryCart> {
        return recentCache.snapshot().values.toList()
    }
}
