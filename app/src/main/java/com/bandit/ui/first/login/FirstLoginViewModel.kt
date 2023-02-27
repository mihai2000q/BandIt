package com.bandit.ui.first.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.di.DILocator

class FirstLoginViewModel : ViewModel() {
    private val _database = DILocator.getDatabase()
    private val _auth = DILocator.getAuthenticator()
    val auth get() = _auth
    val database get() = _database
    val name = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val role = MutableLiveData<BandItEnums.Account.Role>()

    companion object {
        const val TAG = Constants.FirstLogin.VIEW_MODEL_TAG
    }
}