package com.example.firebaseapp.screens

import android.util.Log
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
import com.example.firebaseapp.utils.Constants.MESSAGES
import com.example.firebaseapp.utils.Constants.ROOMS
import com.example.firebaseapp.utils.Constants.USERS
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatRoomScreen(
    userData: MutableState<User>,
    db: FirebaseFirestore,
) {
    val messages = remember { mutableStateListOf<String>() }
    val text = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection(MESSAGES)
            .orderBy("timestamp")
            .addSnapshotListener { value, _ ->
                value?.let {
                    messages.clear()
                    for (doc in it.documents) {
                        messages.add(doc.getString("text") ?: "")
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        db.collection(USERS)
            .orderBy("name")
            .addSnapshotListener { value, _ ->
                value?.let {
                    for (doc in it.documents) {
                        if (doc.getString("uid") != userData.value.uid) {
                            Log.i(
                                "mLogFire",
                                "User: ${doc.getString("name")} ${doc.getString("email")}"
                            )
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
//                val message = Message(senderId = userData.value.uid, content = text.value, timestamp = System.currentTimeMillis())
//                db.collection(ROOMS).document(roomId).collection(MESSAGES).add(message)

                text.value = ""
            }) {
                Text(text = "Send")
            }
        }
    }
}
