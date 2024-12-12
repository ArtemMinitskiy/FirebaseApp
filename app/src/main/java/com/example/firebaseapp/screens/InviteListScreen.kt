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
import com.example.firebaseapp.firebase.asFlow
import com.example.firebaseapp.firebase.getUser
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.ACCEPTED
import com.example.firebaseapp.utils.Constants.FROM
import com.example.firebaseapp.utils.Constants.INVITATIONS
import com.example.firebaseapp.utils.Constants.INVITE_ID
import com.example.firebaseapp.utils.Constants.PENDING
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.utils.Constants.ROOM_ID
import com.example.firebaseapp.utils.Constants.STATUS
import com.example.firebaseapp.utils.Constants.TO
import com.example.firebaseapp.views.UserInvitationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest

@Composable
fun InviteListScreen(
    userData: MutableState<User>,
    db: FirebaseFirestore
) {
    val users = remember { mutableStateListOf<User>() }
    val inviteId = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val invitationsRef = db.collection(INVITATIONS)
        val query = invitationsRef
            .whereEqualTo(TO, userData.value.uid)
            .whereEqualTo(STATUS, PENDING)

        query.asFlow().collectLatest { snapshot ->
            users.clear()
            for (doc in snapshot.documents) {
//                Log.i("mLogFire", "INVITATIONS: ${doc.getString(INVITE_ID).toString()}")
                inviteId.value = doc.getString(INVITE_ID).toString()
                getUser(db, doc.getString(FROM).toString()) { user ->
                    users.add(user)
                }
            }
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
                                db.collection(INVITATIONS).document(inviteId.value)
                            inviteRef.update(
                                STATUS,
                                ACCEPTED,
                                ROOM_ID,
                                roomRef.id,
                                INVITE_ID,
                                inviteId.value
                            )
                        }
                    }, reject = {
                        Log.i("mLogFire", "Reject Invite From User: ${it.name}")
                        Log.e("mLogFire", "inviteId: ${inviteId.value}")
                        db.collection(INVITATIONS).document(inviteId.value).delete()
                    }
                )
            }
        }
    }
}