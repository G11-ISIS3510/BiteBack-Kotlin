package com.kotlin.biteback.data.model

import com.google.firebase.firestore.DocumentId

data class Business(
    @DocumentId val documentId: String? = null,
    val address: String? = null,
    val closeHour: Int? = null,
    val image: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val name: String? = null
)
