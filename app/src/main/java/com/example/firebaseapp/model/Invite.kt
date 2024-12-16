package com.example.firebaseapp.model

data class Invite(
    val inviteId: String = "",
    val from: String = "",
    val fromEmail: String = "",
    val fromName: String = "",
    val fromPicture: String = "",
    val to: String = "",
    val roomId: String? = null,
    val status: String = ""
)
