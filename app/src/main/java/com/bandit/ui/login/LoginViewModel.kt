package com.bandit.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.di.DILocator

class LoginViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email
    private val _authenticator = DILocator.authenticator
    init {
        if(_authenticator.currentUser != null) {
            _email.value = _authenticator.currentUser?.email
        }
    }
    fun signInWithEmailAndPassword(email: String?, password: String?,
                                   onSuccess: (() -> Unit)? = null, onFailure: (() -> Unit)? = null) {
        if(_authenticator.signInWithEmailAndPassword(email ?: "", password ?: "") == true)
            onSuccess?.invoke()
        else
            onFailure?.invoke()
    }
}