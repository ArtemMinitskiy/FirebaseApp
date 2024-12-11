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
import com.example.firebaseapp.firebase.getUser
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.views.ChatView
import com.example.firebaseapp.views.UserView
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RoomsListScreen(
    userData: MutableState<User>,
    db: FirebaseFirestore,
    onChat: () -> Unit
) {
    val rooms = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        db.collection(ROOMS)
            .addSnapshotListener { value, _ ->
                value?.let {
                    for (doc in it.documents) {
                        Log.i("mLogFire", "ROOM: ${doc.id}")
                        Log.i("mLogFire", "ROOM: ${doc.data?.values}")

                        rooms.clear()
                        if (doc.data?.get("createdBy") != userData.value.uid) {
                            getUser(db, doc.data?.get("createdBy").toString()) { user ->
                                Log.e("mLogFire", "USER ROOM: $user")
                                rooms.add(user)
                            }
                        } else {
                            getUser(
                                db,
                                doc.data?.get("participants").toString().split(" ").get(1)
                                    .substringBefore("]")
                            ) { user ->
                                Log.e("mLogFire", "USER ROOM: $user")
                                rooms.add(user)
                            }
                        }
                    }
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(rooms) {
                ChatView(it) {
                    onChat()
                }
            }
        }
    }
}