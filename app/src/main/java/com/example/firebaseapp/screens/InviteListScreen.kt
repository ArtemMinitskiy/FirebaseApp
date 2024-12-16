package com.example.firebaseapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.firebaseapp.MainViewModel
import com.example.firebaseapp.views.UserInvitationView

@Composable
fun InviteListScreen(
    mainViewModel: MainViewModel
) {
    val usersInvites by mainViewModel.invites.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(usersInvites) {
                UserInvitationView(
                    it,
                    accept = {
                        Log.i("mLogFire", "Accept Invite From User: ${it}")
                        mainViewModel.createRoom(it)
                    }, reject = {
                        Log.i("mLogFire", "Reject Invite From User: ${it}")
                        mainViewModel.deleteInvite(it)
                    }
                )
            }
        }
    }
}