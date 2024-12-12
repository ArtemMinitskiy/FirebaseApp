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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.firebaseapp.firebase.getUsersList
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.INVITATIONS
import com.example.firebaseapp.utils.Constants.PENDING
import com.example.firebaseapp.views.UserView
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UsersListScreen(
    userData: MutableState<User>,
    db: FirebaseFirestore
) {
    val users = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        getUsersList(db, userData.value.uid) { user ->
            users.clear()
            users.add(user)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(users) {
                UserView(it) {
                    Log.i("mLogFire", "Invite User: ${it.name}")
                    val roomId = "${userData.value.uid}_${it.uid}"
                    val inviteRef = db.collection(INVITATIONS).document()
                    val inviteId = inviteRef.id
                    val invite = Invite(
                        inviteId = inviteId,
                        from = userData.value.uid,
                        to = it.uid,
                        roomId = roomId,
                        status = PENDING
                    )
                    inviteRef.set(invite)
                        .addOnSuccessListener {
                            Log.i("mLogFire", "Send Invite to User")
                        }
                        .addOnFailureListener {
                            Log.e("mLogFire", "Failure Invite to User")
                        }
                }
            }
        }
    }
}