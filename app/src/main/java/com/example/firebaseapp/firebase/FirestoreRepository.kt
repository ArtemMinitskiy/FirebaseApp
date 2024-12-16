package com.example.firebaseapp.firebase

import android.util.Log
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.utils.Constants.ACCEPTED
import com.example.firebaseapp.utils.Constants.INVITATIONS
import com.example.firebaseapp.utils.Constants.INVITE_ID
import com.example.firebaseapp.utils.Constants.NAME
import com.example.firebaseapp.utils.Constants.PENDING
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.utils.Constants.ROOM_ID
import com.example.firebaseapp.utils.Constants.STATUS
import com.example.firebaseapp.utils.Constants.TO
import com.example.firebaseapp.utils.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirestoreRepository @Inject constructor() {
    private val db = Firebase.firestore

    fun getUsersList(): Flow<QuerySnapshot> {
        val usersRef = db.collection(USERS).orderBy(NAME)
        return usersRef.asFlow()
    }

    fun invite(
        fromUid: String,
        fromEmail: String,
        fromName: String,
        fromPicture: String,
        toUid: String,
        toName: String
    ) {
//        Log.i("mLogFire", "Invite User: $toName")
        val roomId = "${fromUid}_${toUid}"
        val inviteRef = db.collection(INVITATIONS).document()
        val inviteId = inviteRef.id
        val invite = Invite(
            inviteId = inviteId,
            from = fromUid,
            fromEmail = fromEmail,
            fromName = fromName,
            fromPicture = fromPicture,
            to = toUid,
            roomId = roomId,
            status = PENDING
        )
        inviteRef.set(invite)
            .addOnSuccessListener {
                Log.i("mLogFire", "Send Invite to User: $invite")
            }
            .addOnFailureListener {
                Log.e("mLogFire", "Failure Invite to User: $invite ${it.message}")
            }
    }

    fun getInvitesList(
        currentUserUid: String,
        success: (QuerySnapshot) -> Unit,
        ex: (FirebaseFirestoreException) -> Unit
    ) {
        db.collection(INVITATIONS)
            .whereEqualTo(TO, currentUserUid)
            .whereEqualTo(STATUS, PENDING).addSnapshotListener { value, e ->
                if (e != null) {
                    ex(e)
                } else {
                    value?.let { success(it) }
                }
            }
    }

    fun createRoom(invite: Invite) {
        val roomRef = db.collection(ROOMS).document()
        val roomId = roomRef.id
        val room = Room(
            id = roomId,
            roomName = "Private Room",
            createdBy = invite.from,
            participants = listOf(invite.from, invite.to)
        )
        roomRef.set(room)
            .addOnSuccessListener {
                val inviteRef =
                    db.collection(INVITATIONS).document(invite.inviteId)
                inviteRef.update(
                    STATUS,
                    ACCEPTED,
                    ROOM_ID,
                    roomRef.id,
                    INVITE_ID,
                    invite.inviteId
                )
                Log.i("mLogFire", "Create Room: $room")
            }
            .addOnFailureListener {
                Log.e("mLogFire", "Failure Create Room: $invite ${it.message}")
            }
    }

    fun deleteInvite(invite: Invite) {
        db.collection(INVITATIONS).document(invite.inviteId).delete()
    }

}