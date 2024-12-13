package com.example.firebaseapp

import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.UID
import com.google.firebase.firestore.DocumentSnapshot

class UserMapper {
    fun mapUsers(documents: MutableList<DocumentSnapshot>, currentUserUID: String?): List<User> {
        val usersList = arrayListOf<User>()
        for (doc in documents) {
            if (doc.getString(UID) != currentUserUID) {
                usersList.add(
                    User(
                        uid = doc.getString("uid").toString(),
                        email = doc.getString("email").toString(),
                        picture = doc.getString("picture").toString(),
                        name = doc.getString("name").toString(),
                        listOfRooms = arrayListOf()
                    )
                )
            }

        }
        return usersList
    }
}