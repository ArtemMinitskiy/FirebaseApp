package com.example.firebaseapp.firebase

import android.util.Log
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.utils.Constants.FROM
import com.example.firebaseapp.utils.Constants.INVITATIONS
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
        .addSnapshotListener { value, _ ->
            if (value?.isEmpty == true) {
                clear()
            } else {
                value?.let {
                    for (doc in it.documents) {
                        Log.i("mLogFire", "INVITATIONS: $doc")
                        if (doc.getString(FROM) != currentUserUID) {
                            addNewInvitation(
                                Invite(
                                    inviteId = doc.id,
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
        }
}
