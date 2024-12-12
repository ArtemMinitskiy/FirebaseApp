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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.firebaseapp.model.Message
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.CONTENT
import com.example.firebaseapp.utils.Constants.MESSAGES
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.utils.Constants.TIMESTAMP
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatRoomScreen(
    userData: MutableState<User>,
    db: FirebaseFirestore,
    roomId: String?,
) {
    val messages = remember { mutableStateListOf<String>() }
    val text = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        roomId?.let {
            db.collection(ROOMS).document(it).collection(MESSAGES)
                .orderBy(TIMESTAMP)
                .addSnapshotListener { value, _ ->
                    value?.let {
                        messages.clear()
                        for (doc in it.documents) {
                            messages.add(doc.getString(CONTENT) ?: "")
                        }
                    }
                }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(messages) { msg ->
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
//                Log.i("mLogFire", "roomId: $roomId")
//                Log.i("mLogFire", "text: ${text.value}")
                val message = Message(
                    senderId = userData.value.uid,
                    content = text.value,
                    timestamp = System.currentTimeMillis()
                )
                roomId?.let { db.collection(ROOMS).document(it).collection(MESSAGES).add(message) }

                text.value = ""
            }) {
                Text(text = "Send")
            }
        }
    }
}
