package com.example.firebaseapp.firebase

import android.util.Log
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.utils.Constants.FROM
import com.example.firebaseapp.utils.Constants.INVITATIONS
import com.example.firebaseapp.utils.Constants.INVITE_ID
import com.example.firebaseapp.utils.Constants.PENDING
import com.example.firebaseapp.utils.Constants.ROOM_ID
import com.example.firebaseapp.utils.Constants.STATUS
import com.example.firebaseapp.utils.Constants.TO
import com.google.firebase.firestore.FirebaseFirestore

fun getInvitationsList(
    db: FirebaseFirestore,
    currentUserUID: String,
    addNewInvitation: (Invite) -> Unit,
    clear: () -> Unit,
) {
    db.collection(INVITATIONS)
        .whereEqualTo(TO, currentUserUID)
        .whereEqualTo(STATUS, PENDING)
        .get()
        .addOnSuccessListener { snapshot ->
            if (snapshot?.isEmpty == true) {
                clear()
            } else {
                for (doc in snapshot.documents) {
                    Log.i("mLogFire", "INVITATIONS: $doc")
                    addNewInvitation(
                        Invite(
                            inviteId = doc.getString(INVITE_ID).toString(),
                            from = doc.getString(FROM).toString(),
                            to = doc.getString(TO).toString(),
                            roomId = doc.getString(ROOM_ID).toString(),
                            status = doc.getString(STATUS).toString()
                        )
                    )
                }
            }
        }
}
