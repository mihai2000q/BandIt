package com.bandit.ui.first.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class FirstLoginViewModel : ViewModel() {
    private val _auth = DILocator.authenticator
    private val _database = DILocator.database
    val name = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val role = MutableLiveData<BandItEnums.Account.Role>()
    val rememberMe = MutableLiveData<Boolean>()
    fun createAccount() {
        viewModelScope.launch {
            _database.add(
                Account(
                    name.value!!,
                    nickname.value!!,
                    role.value!!,
                    null,
                    _auth.currentUser?.email ?: "",
                    userUid = _auth.currentUser?.uid
                )
            )
        }
    }

    companion object {
        const val TAG = Constants.FirstLogin.VIEW_MODEL_TAG
    }
}