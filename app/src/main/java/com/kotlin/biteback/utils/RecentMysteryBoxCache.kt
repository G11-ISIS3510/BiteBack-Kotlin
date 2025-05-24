package com.kotlin.biteback.utils

import com.kotlin.biteback.data.model.MysteryCart

class RecentMysteryBoxCache(private val maxSize: Int = 5) {
    private val cache = object : LinkedHashMap<String, MysteryCart>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<String, MysteryCart>): Boolean {
            return size > maxSize
        }
    }

    fun add(box: MysteryCart) {
        cache[box.id] = box
    }

    fun getAll(): List<MysteryCart> = cache.values.toList()

    fun clear() {
        cache.clear()
    }
}
