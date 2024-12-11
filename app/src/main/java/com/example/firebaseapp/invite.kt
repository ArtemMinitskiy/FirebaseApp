package com.example.firebaseapp

import android.util.Log
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.model.User
import com.google.firebase.firestore.FirebaseFirestore

fun invite(db: FirebaseFirestore, room: Room, room_uid: String, user: User) {
    db.collection("rooms").document(room_uid)
        .set(room)
        .addOnSuccessListener {
            Log.e("mLogFire", "Success")
        }.addOnFailureListener { e ->
            Log.e("mLogFire", "Failure $e")
        }
    db.collection("users").document(user.uid)
        .set(user)
        .addOnSuccessListener {
            Log.e("mLogFire", "Success")
        }.addOnFailureListener { e ->
            Log.e("mLogFire", "Failure $e")
        }
}
