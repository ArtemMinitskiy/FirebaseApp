package com.example.firebaseapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebaseapp.firebase.FirestoreRepository
import com.example.firebaseapp.mappers.InviteMapper
import com.example.firebaseapp.mappers.RoomMapper
import com.example.firebaseapp.mappers.UserMapper
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.model.InviteTest
import com.example.firebaseapp.model.RoomTest
import com.example.firebaseapp.model.User
import com.example.firebaseapp.utils.Constants.CREATED_BY
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
) : ViewModel() {
    private val _userAuth = MutableLiveData<FirebaseUser?>(null)
    var userAuth: LiveData<FirebaseUser?> = _userAuth
    private val _currentUserUID = MutableLiveData("")
    var currentUserUID: LiveData<String> = _currentUserUID
    private val _users = MutableStateFlow<List<User>>(listOf())
    var users: StateFlow<List<User>> = _users

    init {
        userAuth.observeForever { auth ->
            viewModelScope.launch(Dispatchers.IO) {
                if (auth != null) getUsersList()
            }
        }
    }

    fun addUser(result: AuthResult) {
        firestoreRepository.addUser(result)
    }

    fun setCurrentUserUID(currentUserUID: String) {
        _currentUserUID.value = currentUserUID
    }

    fun setUserAuth(userAuth: FirebaseUser?) {
        _userAuth.value = userAuth
    }

    fun getUser(userUid: String, getUser: (User) -> Unit) {
        firestoreRepository.getUser(userUid, getUser = {
            getUser(it)
        })
    }

    private suspend fun getUsersList() {
        firestoreRepository.getUsersList().collectLatest { snapshot ->
            snapshot.let {
                _users.emit(UserMapper().mapUsers(it.documents, currentUserUID.value))
            }
        }
    }

    fun invite(
        fromUid: String,
        fromEmail: String,
        fromName: String,
        fromPicture: String,
        toUid: String,
        toName: String
    ) {
        firestoreRepository.invite(fromUid, fromEmail, fromName, fromPicture, toUid, toName)
    }

    fun invite2(
        userFrom: User,
        userTo: User
    ) {
        firestoreRepository.invite2(userFrom, userTo)
    }

    private val _invites = MutableStateFlow<List<InviteTest?>>(listOf())
    var invites: StateFlow<List<InviteTest?>> = _invites

    fun getInvitesList(currentUserUid: String) {
//        firestoreRepository.getInvitesList(currentUserUid, success = { snapshot ->
//            snapshot.let {
//                _invites.value = InviteMapper().mapInvite(it.documents)
//            }
//        }, ex = {
//
//        })
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepository.fetchInvitations(success = { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
//                    document.toObject(InviteTest::class.java)?.copy(
//                        inviteId = document.id // Ensure inviteId is set to the document ID
//                    )
                    _invites.value = InviteMapper().mapInvite2(querySnapshot.documents)
                }
            }, ex = {

            })
        }
    }

    fun createRoom(invite: InviteTest) {
        firestoreRepository.createRoom(invite)
    }

    fun deleteInvite(invite: InviteTest) {
        firestoreRepository.deleteInvite(invite)
    }

    private val _usersRooms = MutableStateFlow<List<User>>(arrayListOf())
    var usersRooms: StateFlow<List<User>> = _usersRooms

    fun getRoomsList(
        currentUserUid: String,
    ) {
        firestoreRepository.getRoomsList(success = { snapshot ->
            snapshot.let {
                RoomMapper().mapRoom(currentUserUid, it.documents).forEach { uid ->
                    firestoreRepository.getUserByUid(uid) { user ->
                        _usersRooms.update { currentItems ->
                            currentItems.toMutableList().apply {
                                this.addAll(listOf(user))
                            }
                        }
                    }
                }
            }
        }, ex = {

        })
    }

    private val _usersRooms2 = MutableStateFlow<List<RoomTest?>>(arrayListOf())
    var usersRooms2: StateFlow<List<RoomTest?>> = _usersRooms2
    fun getRoomsList2() {
        firestoreRepository.getRoomsList(success = { querySnapshot ->
            querySnapshot.documents.mapNotNull { document ->
                _usersRooms2.value = RoomMapper().mapRoom2(querySnapshot.documents)
            }
        }, ex = {

        })
    }
}