package com.bandit.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    fun createUser(password: String) {
        viewModelScope.launch {
            launch {
                DILocator.authenticator.createUser(
                    email.value!!,
                    password
                )
            }.join()
            launch { DILocator.database.setUserAccountSetup(false) }.join()
        }
    }

    companion object {
        const val TAG = Constants.Signup.VIEW_MODEL_TAG
    }
}