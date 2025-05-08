package com.kotlin.biteback.utils

import android.content.Context
import java.io.File

object FileStorage {

    fun appendEmailToFile(context: Context, email: String) {
        val file = File(context.filesDir, "emails.txt")
        file.appendText("$email\n")
    }

    fun getEmails(context: Context): List<String> {
        val file = File(context.filesDir, "emails.txt")
        return if (file.exists()) file.readLines() else emptyList()
    }
}
