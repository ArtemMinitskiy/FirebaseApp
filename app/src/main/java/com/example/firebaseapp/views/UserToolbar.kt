package com.example.firebaseapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebaseapp.model.User
import com.example.firebaseapp.ui.theme.Blue
import com.example.firebaseapp.ui.theme.Orange
import com.example.firebaseapp.ui.theme.PurpleGrey40
import com.example.firebaseapp.utils.noRippleClickable

@Composable
fun UserToolbar(userData: MutableState<User>, signOut: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Orange)
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 16.dp)
    ) {
        AsyncImage(
            model = userData.value.picture,
            contentDescription = "Image from File",
            modifier = Modifier
                .size(60.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(userData.value.name)
        Spacer(modifier = Modifier.weight(1f))
        Text("Sign out", modifier = Modifier
            .border(3.dp, PurpleGrey40, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp)
            .padding(vertical = 4.dp)
            .wrapContentSize()
            .noRippleClickable {
                signOut()
            })
    }
}