package com.example.firebaseapp.model

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val picture: String = "",
    val timestamp: Long = 0,
)
