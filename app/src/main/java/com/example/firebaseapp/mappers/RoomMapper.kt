package com.example.firebaseapp.mappers

import android.util.Log
import com.example.firebaseapp.model.InviteTest
import com.example.firebaseapp.model.RoomTest
import com.example.firebaseapp.utils.Constants.CREATED_BY
import com.google.firebase.firestore.DocumentSnapshot

class RoomMapper {
    fun mapRoom(currentUserUid: String, documents: MutableList<DocumentSnapshot>): List<String> {
        val roomsIdList = arrayListOf<String>()
        for (doc in documents) {
            if (doc.data?.get(CREATED_BY) != currentUserUid) {
                roomsIdList.add(
                    doc.getString(CREATED_BY).toString()
                )
            } else {
                val participants = doc.get("participants") as? List<String>
                participants?.get(1)?.let { id ->
                    roomsIdList.add(id)
                }
            }
        }

        return roomsIdList
    }

    fun mapRoom2(documents: MutableList<DocumentSnapshot>): List<RoomTest?> {
        val roomsIdList = arrayListOf<RoomTest?>()
        for (doc in documents) {
            documents.mapNotNull { document ->
                roomsIdList.add(
                    document.toObject(RoomTest::class.java)?.copy(
                        roomId = document.id
                    )
                )
            }
            Log.i("mLogRoom", "Room: $roomsIdList")
        }

        return roomsIdList
    }
}