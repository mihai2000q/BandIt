package com.bandit.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.Constants
import com.bandit.data.repository.AccountRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    suspend fun createUser(password: String) {
        coroutineScope {
            launch { DILocator.getAuthenticator().createUser(email.value!!, password) }.join()
            launch { AccountRepository.setUserAccountSetup(
                DILocator.getDatabase(),
                DILocator.getAuthenticator(),
                false
            ) }.join()
        }
    }

    companion object {
        const val TAG = Constants.Signup.VIEW_MODEL_TAG
    }
}