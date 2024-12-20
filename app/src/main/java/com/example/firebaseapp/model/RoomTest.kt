package com.example.firebaseapp.model

data class RoomTest(
    val id: String = "",
    val roomName: String = "",
    val userFrom: User = User(),
    val userTo: User = User(),
)
