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
import com.example.firebaseapp.invite
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.model.User
import com.example.firebaseapp.views.UserView
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UsersListScreen(
    userData: MutableState<User>,
    db: FirebaseFirestore
) {
    val users = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        db.collection("users")
            .orderBy("name")
            .addSnapshotListener { value, _ ->
                value?.let {
                    for (doc in it.documents) {
                        if (doc.getString("uid") != userData.value.uid) {
                            users.add(
                                User(
                                    uid = doc.getString("uid").toString(),
                                    email = doc.getString("email").toString(),
                                    picture = doc.getString("picture").toString(),
                                    name = doc.getString("name").toString(),
                                    listOfRooms = arrayListOf()
                                )
                            )
                        }
                    }
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(users) {
                UserView(it) {
                    Log.i("mLogFire", "Invite User: ${it.name}")
                    val room_uid = "${userData.value.uid}_${it.uid}"
                    Log.i("mLogFire", "Room UID: $room_uid")
                    val room = Room(
                        id = room_uid,
                        roomCreatorUid = userData.value.uid,
                        listOfUsersId = listOf(userData.value.uid, it.uid)
                    )
                    val listOfRooms = it.listOfRooms
                    listOfRooms.add(room_uid)
                    invite(db, room, room_uid, it.copy(listOfRooms = listOfRooms))
                }
            }
        }
    }
}