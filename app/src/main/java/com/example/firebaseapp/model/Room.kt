package com.example.firebaseapp.model

data class Room(val id: String, val roomCreatorUid: String,
                val listOfUsersId: List<String>
)
