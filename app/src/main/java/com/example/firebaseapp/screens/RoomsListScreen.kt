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
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.ROOMS
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RoomsListScreen(
    userData: MutableState<User>,
    db: FirebaseFirestore
) {
    val rooms = remember { mutableStateListOf<Room>() }

    LaunchedEffect(Unit) {
        db.collection(ROOMS)
            .addSnapshotListener { value, _ ->
                value?.let {
                    for (doc in it.documents) {
                        Log.i("mLogFire", "ROOM: $doc")
                        if (doc.getString("uid") != userData.value.uid) {
                            rooms.add(
                                Room()
                            )
                        }
                    }
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(rooms) {

            }
        }
    }
}