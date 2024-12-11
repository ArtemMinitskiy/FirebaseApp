package com.example.firebaseapp

data class Room(val id: String, val roomCreatorUid: String,
                val listOfUsersId: List<String>
)
