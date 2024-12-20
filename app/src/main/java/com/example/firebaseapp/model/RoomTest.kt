package com.example.firebaseapp.model

data class RoomTest(
    val roomId: String = "",
    val roomName: String = "",
    val createdBy: String = "",
    val userFrom: User = User(),
    val userTo: User = User(),
)
