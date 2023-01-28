package com.bandit.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    fun createUser() {
        viewModelScope.launch {
            launch {
                DILocator.authenticator.createUser(
                    email.value!!,
                    password.value!!
                )
            }.join()
            launch { DILocator.database.setUserAccountSetup(false) }.join()
        }
    }

    companion object {
        const val TAG = Constants.Signup.VIEW_MODEL_TAG
    }
}