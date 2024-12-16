package com.example.firebaseapp.mappers

import android.util.Log
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.utils.Constants.FROM
import com.example.firebaseapp.utils.Constants.FROM_EMAIL
import com.example.firebaseapp.utils.Constants.FROM_NAME
import com.example.firebaseapp.utils.Constants.FROM_PICTURE
import com.example.firebaseapp.utils.Constants.INVITE_ID
import com.example.firebaseapp.utils.Constants.ROOM_ID
import com.example.firebaseapp.utils.Constants.STATUS
import com.example.firebaseapp.utils.Constants.TO
import com.google.firebase.firestore.DocumentSnapshot

class InviteMapper {
    fun mapInvite(documents: MutableList<DocumentSnapshot>): List<Invite> {
        val invitesList = arrayListOf<Invite>()
        for (doc in documents) {
            invitesList.add(
                Invite(
                    inviteId = doc.getString(INVITE_ID).toString(),
                    from = doc.getString(FROM).toString(),
                    fromEmail = doc.getString(FROM_EMAIL).toString(),
                    fromName = doc.getString(FROM_NAME).toString(),
                    fromPicture = doc.getString(FROM_PICTURE).toString(),
                    to = doc.getString(TO).toString(),
                    roomId = doc.getString(ROOM_ID).toString(),
                    status = doc.getString(STATUS).toString()
                )
            )
        }

        Log.i("mLogInvite", "Invites: $invitesList")
        return invitesList
    }
}