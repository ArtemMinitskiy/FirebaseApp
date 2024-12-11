package com.example.firebaseapp.firebase

import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.UID
import com.example.firebaseapp.utils.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore

fun getUsersList(db: FirebaseFirestore, currentUserUID: String, addNewUser: (User) -> Unit) {
    db.collection(USERS)
        .orderBy("name")
        .addSnapshotListener { value, _ ->
            value?.let {
                for (doc in it.documents) {
                    if (doc.getString(UID) != currentUserUID) {
                        addNewUser(
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
            }
        }
}