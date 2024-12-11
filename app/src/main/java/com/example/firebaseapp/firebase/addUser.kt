package com.example.firebaseapp.firebase

import android.util.Log
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.USERS
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore

fun addUser(db: FirebaseFirestore, result: AuthResult) {
    db.collection(USERS).document(result.user?.uid.toString()).set(
        User(
            uid = result.user?.uid.toString(),
            email = result.additionalUserInfo?.profile?.get("email").toString(),
            picture = result.additionalUserInfo?.profile?.get("picture").toString(),
            name = result.additionalUserInfo?.profile?.get("name").toString(),
            timestamp = System.currentTimeMillis(),
            listOfRooms = arrayListOf()
        )
    ).addOnSuccessListener {
        Log.e("mLogFire", "Add User Success")
    }.addOnFailureListener { e ->
        Log.e("mLogFire", "Add User Failure $e")
    }
}