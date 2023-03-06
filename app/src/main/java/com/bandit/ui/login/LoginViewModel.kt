package com.bandit.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class LoginViewModel : ViewModel() {
    private val _database = DILocator.getDatabase()
    private val _auth = DILocator.getAuthenticator()
    val auth get() = _auth
    val database get() = _database
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email
    init {
        if(_auth.currentUser != null)
            _email.value = _auth.currentUser?.email
    }
    suspend fun signInWithEmailAndPassword(
        email: String?,
        password: String?,
        onSuccess: (suspend () -> Unit)? = null,
        onFailure: (() -> Unit)? = null
    ) = coroutineScope {
        if (_auth.signInWithEmailAndPassword(
                email ?: "",
                password ?: ""
            ) == true
        ) {
            Log.i(TAG, "User logged in")
            onSuccess?.invoke()
        } else {
            Log.w(TAG, "User couldn't log in")
            onFailure?.invoke()
        }
    }

    companion object {
        const val TAG = Constants.Login.VIEW_MODEL_TAG
    }
}