package com.example.firebaseapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import coil.compose.AsyncImage
import com.example.firebaseapp.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatRoomScreen(
    user: MutableState<FirebaseUser?>,
    userData: MutableState<User>,
    db: FirebaseFirestore
) {
    val messages = remember { mutableStateListOf<String>() }
    val message = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection("messages")
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
        db.collection("users")
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

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = 16.dp, top = 8.dp)
        ) {
            AsyncImage(
                model = userData.value.picture,
                contentDescription = "Image from File",
                modifier = Modifier
                    .size(60.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(userData.value.name)
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(messages) { msg ->
                Text(text = msg, modifier = Modifier.padding(8.dp))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.wrapContentHeight()) {
            TextField(
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                db.collection("messages").add(
                    mapOf(
                        "text" to message.value,
                        "user" to user.value?.displayName,
                        "timestamp" to System.currentTimeMillis()
                    )
                )
                message.value = ""
            }) {
                Text(text = "Send")
            }
        }
    }
}
