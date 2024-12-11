package com.example.firebaseapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cleanease.optimize.navigation.NavigationItem
import com.example.firebaseapp.model.User
import com.example.firebaseapp.screens.SignInScreen
import com.example.firebaseapp.screens.UsersListScreen
import com.example.firebaseapp.ui.theme.FirebaseAppTheme
import com.example.firebaseapp.views.UserToolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val user = remember { mutableStateOf(Firebase.auth.currentUser) }
            val db = Firebase.firestore
            val userData = remember { mutableStateOf(User()) }

            LaunchedEffect(user.value) {
                Log.i("mLogFire", "User: ${user.value?.uid}")
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
                        Log.e("mLogFire", "Add User Success")
                    }.addOnFailureListener { e ->
                        Log.e("mLogFire", "Add User Failure $e")
                    }
                },
                onAuthError = {
                    user.value = null
                }
            )
            FirebaseAppTheme {
                Column {
                    UserToolbar(userData)
                    Spacer(modifier = Modifier.height(16.dp))

                    NavHost(navController = navController,
                        startDestination = if (user.value == null) NavigationItem.SignIn.route else NavigationItem.UsersList.route,
                        exitTransition = {
                            ExitTransition.None
                        },
                        popExitTransition = {
                            ExitTransition.None
                        }
                    ) {
                        composable(NavigationItem.SignIn.route) {
                            SignInScreen(
                                modifier = Modifier
                                    .fillMaxSize(),
                                user, launcher
                            )
                        }
                        composable(NavigationItem.UsersList.route) {
                            UsersListScreen(userData, db)
                        }
                    }
                }
            }
        }
    }
}