package com.example.firebaseapp.firebase

import android.util.Log
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.utils.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore

fun invite(db: FirebaseFirestore, room: Room, room_uid: String, user: User) {
    db.collection(ROOMS).document(room_uid)
        .set(room)
        .addOnSuccessListener {
            Log.e("mLogFire", "Success")
        }.addOnFailureListener { e ->
            Log.e("mLogFire", "Failure $e")
        }
    db.collection(USERS).document(user.uid)
        .set(user)
        .addOnSuccessListener {
            Log.e("mLogFire", "Success")
        }.addOnFailureListener { e ->
            Log.e("mLogFire", "Failure $e")
        }
}
