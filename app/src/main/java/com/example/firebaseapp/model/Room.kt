package com.example.firebaseapp.model

data class Room(
    val id: String = "",
    val roomName: String = "",
    val createdBy: String = "",
    val createdByEmail: String = "",
    val createdByName: String = "",
    val createdByPicture: String = "",
    val participants: List<String> = listOf()
)
