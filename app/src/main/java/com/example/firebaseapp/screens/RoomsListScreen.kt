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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.firebaseapp.MainViewModel
import com.example.firebaseapp.firebase.getUser
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.views.ChatView
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RoomsListScreen(
    userData: MutableState<User>,
    mainViewModel: MainViewModel,
    onChat: (String) -> Unit
) {
    val usersRooms by mainViewModel.usersRooms2.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(usersRooms) {
                ChatView(userData.value.uid, it) {
                    Log.i("mLogMessage", "${it?.roomId}")
//                    onChat(it.uid)
//                    onChat(roomId.value)
                }
            }
        }
    }
}