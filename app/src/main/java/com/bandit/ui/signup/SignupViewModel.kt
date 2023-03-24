package com.bandit.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.Constants
import com.bandit.data.repository.AccountRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val _database = DILocator.getDatabase()
    private val _auth = DILocator.getAuthenticator()
    val database get() = _database
    val email = MutableLiveData<String>()
    suspend fun createUser(password: String) = coroutineScope {
        async {
            launch {
                _auth.createUser(email.value!!, password)
                AccountRepository.setUserAccountSetup(
                    _database,
                    _auth,
                    false
                )
            }
        }
    }.await()

    companion object {
        const val TAG = Constants.Signup.VIEW_MODEL_TAG
    }
}