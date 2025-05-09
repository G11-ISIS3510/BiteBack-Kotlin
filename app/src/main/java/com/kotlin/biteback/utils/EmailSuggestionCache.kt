package com.kotlin.biteback.utils

import android.util.LruCache

object EmailSuggestionCache {
    val cache: LruCache<String, String> = LruCache(4)
}
