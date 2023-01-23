package com.bandit.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email
    private val _authenticator = DILocator.authenticator
    init {
        if(_authenticator.currentUser != null)
            _email.value = _authenticator.currentUser?.email
    }
    suspend fun signInWithEmailAndPassword(email: String?,
                                           password: String?,
                                           onSuccess: (() -> Unit)? = null,
                                           onFailure: (() -> Unit)? = null) {
        viewModelScope.launch {
            var result: Boolean? = null
            launch { result = _authenticator.signInWithEmailAndPassword(
                email ?: "",
                password ?: ""
                )
            }.join()
            if (result == true) {
                Log.i(TAG, "User logged in")
                onSuccess?.invoke()
            } else {
                Log.i(TAG, "User couldn't log in")
                onFailure?.invoke()
            }
        }
    }

    companion object {
        const val TAG = Constants.Login.VIEW_MODEL_TAG
    }
}