package com.example.firebaseapp.mappers

import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.EMAIL
import com.example.firebaseapp.utils.Constants.NAME
import com.example.firebaseapp.utils.Constants.PICTURE
import com.example.firebaseapp.utils.Constants.UID
import com.google.firebase.firestore.DocumentSnapshot

class UserMapper {
    fun mapUsers(documents: MutableList<DocumentSnapshot>, currentUserUID: String?): List<User> {
        val usersList = arrayListOf<User>()
        for (doc in documents) {
            if (doc.getString(UID) != currentUserUID) {
                usersList.add(
                    User(
                        uid = doc.getString(UID).toString(),
                        email = doc.getString(EMAIL).toString(),
                        picture = doc.getString(PICTURE).toString(),
                        name = doc.getString(NAME).toString(),
                    )
                )
            }

        }
        return usersList
    }
}