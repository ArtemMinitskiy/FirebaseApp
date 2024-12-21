package com.example.firebaseapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebaseapp.firebase.FirestoreRepository
import com.example.firebaseapp.mappers.InviteMapper
import com.example.firebaseapp.mappers.MessageMapper
import com.example.firebaseapp.mappers.RoomMapper
import com.example.firebaseapp.mappers.UserMapper
import com.example.firebaseapp.model.Invite
import com.example.firebaseapp.model.Message
import com.example.firebaseapp.model.Room
import com.example.firebaseapp.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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

    fun getUserByUid(userUid: String, getUser: (User) -> Unit) {
        firestoreRepository.getUserByUid(userUid, getUser = {
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
        userFrom: User,
        userTo: User
    ) {
        firestoreRepository.invite(userFrom, userTo)
    }

    private val _invites = MutableStateFlow<List<Invite?>>(listOf())
    var invites: StateFlow<List<Invite?>> = _invites
    fun getInvitesList() {
        viewModelScope.launch(Dispatchers.IO) {
            firestoreRepository.getInvitesList(success = { querySnapshot ->
                querySnapshot.documents.mapNotNull {
                    _invites.value = InviteMapper().mapInvite(querySnapshot.documents)
                }
            }, ex = {

            })
        }
    }

    fun createRoom(invite: Invite) {
        firestoreRepository.createRoom(invite)
    }

    fun deleteInvite(invite: Invite) {
        firestoreRepository.deleteInvite(invite)
    }

    private val _usersRooms = MutableStateFlow<List<Room?>>(arrayListOf())
    var usersRooms: StateFlow<List<Room?>> = _usersRooms
    fun getRoomsList2() {
        firestoreRepository.getRoomsList(success = { querySnapshot ->
            querySnapshot.documents.mapNotNull { document ->
                _usersRooms.value = RoomMapper().mapRoom(querySnapshot.documents)
            }
        }, ex = {

        })
    }

    fun sendMessage(roomId: String, message: Message) {
        firestoreRepository.sendMessage(roomId, message)
    }

    private val _messages = MutableStateFlow<List<String>>(listOf())
    var messages: StateFlow<List<String>> = _messages
    fun getMessagesList(roomId: String) {
        firestoreRepository.getMessagesList(roomId, success = { querySnapshot ->
            _messages.value = MessageMapper().mapMessages(querySnapshot.documents)
        }, ex = {

        })
    }

}