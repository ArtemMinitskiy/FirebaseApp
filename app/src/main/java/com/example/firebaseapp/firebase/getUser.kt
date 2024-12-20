package com.example.firebaseapp.firebase

import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.UID
import com.example.firebaseapp.utils.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore

fun getUser(db: FirebaseFirestore, userUID: String, getUser: (User) -> Unit) {
    db.collection(USERS).document(userUID)
        .addSnapshotListener { value, _ ->
            value?.let { doc ->
                getUser(
                    User(
                        uid = doc.getString("uid").toString(),
                        email = doc.getString("email").toString(),
                        picture = doc.getString("picture").toString(),
                        name = doc.getString("name").toString(),
                    )
                )
            }
        }
}