package com.example.firebaseapp.mappers

import com.example.firebaseapp.model.Room
import com.google.firebase.firestore.DocumentSnapshot

class RoomMapper {
    fun mapRoom(documents: MutableList<DocumentSnapshot>): List<Room?> {
        val roomsIdList = arrayListOf<Room?>()
        for (doc in documents) {
            documents.mapNotNull { document ->
                roomsIdList.add(
                    document.toObject(Room::class.java)?.copy(
                        roomId = document.id
                    )
                )
            }
//            Log.i("mLogRoom", "Room: $roomsIdList")
        }

        return roomsIdList
    }
}