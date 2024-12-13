package com.example.firebaseapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebaseapp.firebase.FirestoreRepository
import com.example.firebaseapp.model.User
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

    fun setCurrentUserUID(currentUserUID: String) {
        _currentUserUID.value = currentUserUID
    }

    fun setUserAuth(userAuth: FirebaseUser?) {
        _userAuth.value = userAuth
    }

    private suspend fun getUsersList() {
        firestoreRepository.getUsersList().collectLatest { snapshot ->
            snapshot.let {
                _users.emit(UserMapper().mapUsers(it.documents, currentUserUID.value))
            }
        }
    }

    fun invite(fromUid: String, toUid: String, toName: String) {
        firestoreRepository.invite(fromUid, toUid, toName)
    }
}