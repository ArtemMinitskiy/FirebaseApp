package com.example.firebaseapp.model

data class InviteTest(
    val inviteId: String = "",
    val from: String = "",
    val userFrom: User = User(),
    val userTo: User = User(),
    val to: String = "",
    val roomId: String? = null,
    val status: String = ""
)
