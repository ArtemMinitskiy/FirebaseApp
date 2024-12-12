package com.example.firebaseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.example.firebaseapp.screens.ChatRoomScreen
import com.example.firebaseapp.screens.InviteListScreen
import com.example.firebaseapp.screens.RoomsListScreen
import com.example.firebaseapp.screens.SignInScreen
import com.example.firebaseapp.screens.UsersListScreen
import com.example.firebaseapp.ui.theme.FirebaseAppTheme
import com.example.firebaseapp.ui.theme.PurpleGrey40
import com.example.firebaseapp.utils.Constants.ROOM_ID
import com.example.firebaseapp.utils.noRippleClickable
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
//                Log.i("mLogFire", "User: ${user.value?.uid}")
                user.value?.providerData?.let { data ->
                    userData.value = User(
                        uid = user.value?.uid.toString(),
                        email = data[0].email.toString(),
                        picture = data[0].photoUrl.toString(),
                        name = data[0].displayName.toString(),
                        timestamp = System.currentTimeMillis(),
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
                        UserToolbar(userData, toInvite = {
                            navController.navigate(NavigationItem.InvitesList.route)
                        }) {
                            Firebase.auth.signOut()
                            user.value = null
                        }
                        Row(
                            modifier = Modifier
//                                .background(Orange)
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 16.dp)
                        ) {
                            Text("Invitations", modifier = Modifier
                                .border(3.dp, PurpleGrey40, RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 4.dp)
                                .wrapContentSize()
                                .noRippleClickable {
                                    navController.navigate(NavigationItem.InvitesList.route)
                                })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Room", modifier = Modifier
                                .border(3.dp, PurpleGrey40, RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp)
                                .padding(vertical = 4.dp)
                                .wrapContentSize()
                                .noRippleClickable {
                                    navController.navigate(NavigationItem.RoomsList.route)
                                })
                        }
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
                        composable(NavigationItem.InvitesList.route) {
                            InviteListScreen(userData, db)
                        }
                        composable(NavigationItem.RoomsList.route) {
                            RoomsListScreen(userData, db) { roomId ->
                                navController.navigate("${NavigationItem.Rooms.route}/$roomId")
                            }
                        }
                        composable("${NavigationItem.Rooms.route}/{$ROOM_ID}") { backStackEntry ->
                            val roomId = backStackEntry.arguments?.getString(ROOM_ID)
                            ChatRoomScreen(userData, db, roomId)
                        }
                    }
                }
            }
        }
    }
}