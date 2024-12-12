package com.example.firebaseapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.firebaseapp.firebase.getInvitationsList
import com.example.firebaseapp.firebase.getUser
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.ACCEPTED
import com.example.firebaseapp.utils.Constants.INVITATIONS
import com.example.firebaseapp.utils.Constants.INVITE_ID
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.utils.Constants.ROOM_ID
import com.example.firebaseapp.utils.Constants.STATUS
import com.example.firebaseapp.views.UserInvitationView
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun InviteListScreen(
    userData: MutableState<User>,
    db: FirebaseFirestore
) {
    val users = remember { mutableStateListOf<User>() }
    val invite = remember { mutableStateOf(Invite()) }

    LaunchedEffect(Unit) {
        getInvitationsList(db, userData.value.uid, addNewInvitation = { newInvite ->
            users.clear()
            invite.value = newInvite
            getUser(db, newInvite.from) { user ->
                users.add(user)
            }
        }) {
            users.clear()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(users) {
                UserInvitationView(
                    it,
                    accept = {
                        Log.i("mLogFire", "Accept Invite From User: ${it.name}")
                        val roomRef = db.collection(ROOMS).document()
                        val roomId = roomRef.id
                        val room = Room(
                            id = roomId,
                            roomName = "Private Room",
                            createdBy = userData.value.uid,
                            participants = listOf(userData.value.uid, it.uid)
                        )
                        roomRef.set(room).addOnSuccessListener {
                            val inviteRef =
                                db.collection(INVITATIONS).document(invite.value.inviteId)
                            inviteRef.update(
                                STATUS,
                                ACCEPTED,
                                ROOM_ID,
                                roomRef.id,
                                INVITE_ID,
                                invite.value.inviteId
                            )
                        }
                    }, reject = {
                        Log.i("mLogFire", "Reject Invite From User: ${it.name}")
                        db.collection(INVITATIONS).document(invite.value.inviteId).delete()
                        if (users.size == 1) {
                            users.clear()
                        }
                    }
                )
            }
        }
    }
}