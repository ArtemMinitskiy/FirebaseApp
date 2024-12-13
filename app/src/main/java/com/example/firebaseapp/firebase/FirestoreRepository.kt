package com.example.firebaseapp.firebase

import android.util.Log
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.utils.Constants.INVITATIONS
import com.example.firebaseapp.utils.Constants.PENDING
import com.example.firebaseapp.utils.Constants.USERS
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirestoreRepository @Inject constructor() {
    private val db = Firebase.firestore

    fun getUsersList(): Flow<QuerySnapshot> {
        val usersRef = db.collection(USERS).orderBy("name")
        return usersRef.asFlow()
    }

    fun invite(fromUid: String, toUid: String, toName: String) {
        Log.i("mLogFire", "Invite User: $toName")
        val roomId = "${fromUid}_${toUid}"
        val inviteRef = db.collection(INVITATIONS).document()
        val inviteId = inviteRef.id
        val invite = Invite(
            inviteId = inviteId,
            from = fromUid,
            to = toUid,
            roomId = roomId,
            status = PENDING
        )
        inviteRef.set(invite)
            .addOnSuccessListener {
                Log.i("mLogFire", "Send Invite to User: $toName")
            }
            .addOnFailureListener {
                Log.e("mLogFire", "Failure Invite to User: $toName ${it.message}")
            }
    }
}