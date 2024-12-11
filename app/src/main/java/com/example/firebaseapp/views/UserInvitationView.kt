package com.example.firebaseapp.views

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.noRippleClickable
import com.example.firebaseapp.ui.theme.Blue
import com.example.firebaseapp.ui.theme.Red

@Composable
fun UserInvitationView(user: User, accept: () -> Unit, reject: () -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.picture,
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
            Text(user.name)
            Text(user.email)
            Row(
                modifier = Modifier
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Accept", modifier = Modifier
                    .border(3.dp, Blue, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 4.dp)
                    .wrapContentSize()
                    .noRippleClickable {
                        accept()
                    })
                Spacer(modifier = Modifier.width(16.dp))
                Text("Reject", modifier = Modifier
                    .border(3.dp, Red, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp)
                    .padding(vertical = 4.dp)
                    .wrapContentSize()
                    .noRippleClickable {
                        reject()
                    })
            }

        }
    }
}