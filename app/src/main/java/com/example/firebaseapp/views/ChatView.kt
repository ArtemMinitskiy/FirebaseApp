package com.example.firebaseapp.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.utils.noRippleClickable

@Composable
fun ChatView(currentUserUid: String, user: Room?, onClick: () -> Unit) {
    user?.let {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = 16.dp)
                .noRippleClickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (currentUserUid == user.createdBy) user.userTo.picture else user.userFrom.picture,
                contentDescription = "Image from File",
                modifier = Modifier
                    .size(60.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(if (currentUserUid == user.createdBy) user.userTo.name else user.userFrom.name)
                Text(if (currentUserUid == user.createdBy) user.userTo.email else user.userFrom.email)
            }
        }
    }
}