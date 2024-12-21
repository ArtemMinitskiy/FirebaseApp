package com.example.firebaseapp.mappers

import com.example.firebaseapp.utils.Constants.CONTENT
import com.google.firebase.firestore.DocumentSnapshot

class MessageMapper {
    fun mapMessages(documents: MutableList<DocumentSnapshot>): List<String> {
        val messagesList = arrayListOf<String>()
        for (doc in documents) {
            messagesList.add(doc.getString(CONTENT) ?: "")
        }
        return messagesList
    }
}