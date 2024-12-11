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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cleanease.optimize.navigation.NavigationItem
import com.example.firebaseapp.firebase.addUser
import com.example.firebaseapp.firebase.rememberFirebaseAuthLauncher
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
                user.value?.providerData?.let { data ->
                    userData.value = User(
                        uid = user.value?.uid.toString(),
                        email = data[0].email.toString(),
                        picture = data[0].photoUrl.toString(),
                        name = data[0].displayName.toString(),
                        timestamp = System.currentTimeMillis(),
//                        listOfRooms = it.listOfRooms
                    )
                }
            }

            val launcher = rememberFirebaseAuthLauncher(
                onAuthComplete = { result ->
                    user.value = result.user
                    addUser(db, result)
                },
                onAuthError = {
                    user.value = null
                }
            )
            FirebaseAppTheme {
                Column {
                    if (user.value != null) {
                        UserToolbar(userData) {
                            Firebase.auth.signOut()
                            user.value = null
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

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
                                launcher
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