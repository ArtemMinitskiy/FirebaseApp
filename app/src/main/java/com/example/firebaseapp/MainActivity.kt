package com.example.firebaseapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.firebaseapp.ui.theme.FirebaseAppTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val user = remember { mutableStateOf(Firebase.auth.currentUser) }
            val db = Firebase.firestore
//            val db = FirebaseFirestore.getInstance()
            val userData = remember { mutableStateOf(User()) }

            LaunchedEffect(user.value) {
                Log.i("mLogFire", "user ${user.value?.uid}")
                user.value?.providerData?.forEach {
                    userData.value = User(
                        uid = user.value?.uid.toString(),
                        email = it.email.toString(),
                        picture = it.photoUrl.toString(),
                        name = it.displayName.toString(),
                        timestamp = System.currentTimeMillis(),
//                        listOfRooms = it.listOfRooms
                    )
                }
            }

            val launcher = rememberFirebaseAuthLauncher(
                onAuthComplete = { result ->
                    user.value = result.user
                    db.collection("users").document(result.user?.uid.toString()).set(
                        User(
                            uid = result.user?.uid.toString(),
                            email = result.additionalUserInfo?.profile?.get("email").toString(),
                            picture = result.additionalUserInfo?.profile?.get("picture").toString(),
                            name = result.additionalUserInfo?.profile?.get("name").toString(),
                            timestamp = System.currentTimeMillis(),
                            listOfRooms = arrayListOf()
                        )
                    ).addOnSuccessListener {
                        Log.e("mLogFire", "Success")
                    }.addOnFailureListener { e ->
                        Log.e("mLogFire", "Failure $e")
                    }
//                    val room_uid = "${userData.value.uid}_it.uid"
//                    Log.i("mLogFire", "Room UID: $room_uid")
//                    val room = Room(
//                        id = room_uid,
//                        roomCreatorUid = userData.value.uid,
//                        listOfUsersId = listOf(userData.value.uid, "ewfwefwef")
//                    )
//                    db.collection("rooms").document(room_uid).set(room)
                },
                onAuthError = {
                    user.value = null
                }
            )
            FirebaseAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (user.value == null) {
                        SignInScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            user, launcher
                        )
                    } else {
                        UsersListScreen(user, userData, db)
                    }
                }
            }
        }
    }
}