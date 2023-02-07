package com.bandit.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val _auth = DILocator.authenticator
    private val _database = DILocator.database
    val email = MutableLiveData<String>()
    suspend fun createUser(password: String) {
        viewModelScope.launch {
            launch { _auth.createUser(email.value!!, password) }.join()
            launch { _database.setUserAccountSetup(false) }.join()
        }
    }

    companion object {
        const val TAG = Constants.Signup.VIEW_MODEL_TAG
    }
}