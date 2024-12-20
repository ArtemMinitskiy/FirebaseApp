package com.example.firebaseapp.model

data class InviteTest(
    val inviteId: String = "",
    val userFrom: User = User(),
    val userTo: User = User(),
    val roomId: String? = null,
    val status: String = ""
)
