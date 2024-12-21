package com.example.firebaseapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.firebaseapp.MainViewModel
import com.example.firebaseapp.model.User
import com.example.firebaseapp.views.ChatView

@Composable
fun RoomsListScreen(
    userData: MutableState<User>,
    mainViewModel: MainViewModel,
    onChat: (String) -> Unit
) {
    val usersRooms by mainViewModel.usersRooms.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(usersRooms) { room ->
                ChatView(userData.value.uid, room) {
                    Log.i("mLogMessage", "${room?.roomId}")
                    room?.roomId?.let { it -> onChat(it) }
                }
            }
        }
    }
}