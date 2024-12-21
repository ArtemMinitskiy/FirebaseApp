package com.example.firebaseapp.mappers

import android.util.Log
import com.example.firebaseapp.model.Invite
import com.google.firebase.firestore.DocumentSnapshot

class InviteMapper {
    fun mapInvite(documents: MutableList<DocumentSnapshot>): List<Invite?> {
        val invitesList = arrayListOf<Invite?>()
        for (doc in documents) {
            documents.mapNotNull { document ->
                invitesList.add(
                    document.toObject(Invite::class.java)?.copy(
                        inviteId = document.id
                    )
                )
            }

        }

        Log.i("mLogInvite", "Invites: $invitesList")
        return invitesList
    }
}