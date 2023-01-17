package com.bandit.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.di.DILocator

class SignupViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    fun createUser() {
        DILocator.authenticator.createUser(email.value!!, password.value!!, displayName.value!!)
    }
}