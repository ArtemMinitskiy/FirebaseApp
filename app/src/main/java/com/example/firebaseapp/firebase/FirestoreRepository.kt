package com.example.firebaseapp.firebase

import android.util.Log
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.model.InviteTest
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.model.RoomTest
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.ACCEPTED
import com.example.firebaseapp.utils.Constants.CREATED_BY
import com.example.firebaseapp.utils.Constants.EMAIL
import com.example.firebaseapp.utils.Constants.INVITATIONS
import com.example.firebaseapp.utils.Constants.INVITE_ID
import com.example.firebaseapp.utils.Constants.NAME
import com.example.firebaseapp.utils.Constants.PENDING
import com.example.firebaseapp.utils.Constants.PICTURE
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.utils.Constants.ROOM_ID
import com.example.firebaseapp.utils.Constants.STATUS
import com.example.firebaseapp.utils.Constants.TO
import com.example.firebaseapp.utils.Constants.USERS
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor() {
    private val db = Firebase.firestore

    fun addUser(result: AuthResult) {
        db.collection(USERS).document(result.user?.uid.toString()).set(
            User(
                uid = result.user?.uid.toString(),
                email = result.additionalUserInfo?.profile?.get(EMAIL).toString(),
                picture = result.additionalUserInfo?.profile?.get(PICTURE).toString(),
                name = result.additionalUserInfo?.profile?.get(NAME).toString(),
                timestamp = System.currentTimeMillis(),
            )
        ).addOnSuccessListener {
            Log.e("mLogFire", "Add User Success")
        }.addOnFailureListener { e ->
            Log.e("mLogFire", "Add User Failure $e")
        }
    }

    fun getUsersList(): Flow<QuerySnapshot> {
        val usersRef = db.collection(USERS).orderBy(NAME)
        return usersRef.asFlow()
    }

    fun getUser(userUID: String, getUser: (User) -> Unit) {
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

    fun getUserByUid(userUID: String, addUser: (User) -> Unit) {
        db.collection(USERS).document(userUID)
            .addSnapshotListener { value, _ ->
                value?.let { doc ->
                    Log.i("mLogRoom", "user: $doc")
                    addUser(
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

    fun invite2(
        userFrom: User,
        userTo: User
    ) {
//        Log.i("mLogFire", "Invite User: $toName")
        val roomId = "${userFrom.uid}_${userTo.uid}"
        val inviteRef = db.collection(INVITATIONS).document()
        val inviteId = inviteRef.id
        val invite = InviteTest(
            inviteId = inviteId,
            userFrom = userFrom,
            userTo = userTo,
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


    fun fetchInvitations(
        success: (QuerySnapshot) -> Unit,
        ex: (FirebaseFirestoreException) -> Unit
    ) {
        val querySnapshot =
            db.collection(INVITATIONS).whereEqualTo(STATUS, PENDING)

        querySnapshot.addSnapshotListener { value, e ->
            if (e != null) {
                ex(e)
            } else {
                value?.let { success(it) }
            }
        }
    }


    fun createRoom(invite: InviteTest) {
        val roomRef = db.collection(ROOMS).document()
        val roomId = roomRef.id
        val room = RoomTest(
            roomId = roomId,
            roomName = "Private Room",
            createdBy = invite.userFrom.uid,
            userFrom = invite.userFrom,
            userTo = invite.userTo
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

    fun deleteInvite(invite: InviteTest) {
        db.collection(INVITATIONS).document(invite.inviteId).delete()
    }

    fun getRoomsList(
        success: (QuerySnapshot) -> Unit,
        ex: (FirebaseFirestoreException) -> Unit
    ) {
        db.collection(ROOMS)
            .addSnapshotListener { value, e ->
                value?.let {
                    if (e != null) {
                        ex(e)
                    } else {
                        value.let { success(it) }
                    }
                }
            }
    }
}