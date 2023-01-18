package com.bandit.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    fun createUser() {
        viewModelScope.launch {
            DILocator.authenticator.createUser(
                email.value!!,
                password.value!!,
                displayName.value!!
            )
        }
    }
}