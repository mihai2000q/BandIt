package com.bandit.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val _auth = DILocator.getAuthenticator()
    private val _database = DILocator.getDatabase()
    val email = MutableLiveData<String>()
    suspend fun createUser(password: String) {
        coroutineScope {
            launch { _auth.createUser(email.value!!, password) }.join()
            launch { _database.setUserAccountSetup(
                _auth.currentUser?.uid ?: "",
                _auth.currentUser?.email ?: "",
                false
            ) }.join()
        }
    }

    companion object {
        const val TAG = Constants.Signup.VIEW_MODEL_TAG
    }
}