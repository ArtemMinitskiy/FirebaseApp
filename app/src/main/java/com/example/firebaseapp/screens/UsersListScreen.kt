package com.example.firebaseapp.screens

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
import com.example.firebaseapp.views.UserView

@Composable
fun UsersListScreen(
    userData: MutableState<User>,
    mainViewModel: MainViewModel
) {
    val users by mainViewModel.users.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(users) {
                UserView(it) {
                    mainViewModel.invite(
                        fromUid = userData.value.uid,
                        toUid = it.uid,
                        toName = it.name
                    )
                }
            }
        }
    }
}