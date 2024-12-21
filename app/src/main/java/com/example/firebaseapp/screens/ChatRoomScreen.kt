package com.example.firebaseapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.firebaseapp.MainViewModel
import com.example.firebaseapp.model.Message
import com.example.firebaseapp.model.User

@Composable
fun ChatRoomScreen(
    userData: MutableState<User>,
    mainViewModel: MainViewModel,
    roomId: String?,
) {
    val usersMessages by mainViewModel.messages.collectAsState()
    val text = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(usersMessages) { msg ->
                Text(text = msg, modifier = Modifier.padding(8.dp))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.wrapContentHeight()) {
            TextField(
                value = text.value,
                onValueChange = { text.value = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                val message = Message(
                    senderId = userData.value.uid,
                    content = text.value,
                    timestamp = System.currentTimeMillis()
                )
                roomId?.let { mainViewModel.sendMessage(it, message) }

                text.value = ""
            }) {
                Text(text = "Send")
            }
        }
    }
}
