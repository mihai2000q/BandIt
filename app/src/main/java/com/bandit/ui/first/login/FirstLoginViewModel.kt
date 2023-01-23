package com.bandit.ui.first.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.BandItEnums
import com.bandit.data.model.Account
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class FirstLoginViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val role = MutableLiveData<BandItEnums.Account.Role>()
    val rememberMe = MutableLiveData<Boolean>()
    fun createAccount() {
        viewModelScope.launch {
            DILocator.database.add(
                Account(
                    name.value!!,
                    nickname.value!!,
                    role.value!!,
                    null,
                    userUid = DILocator.authenticator.currentUser?.uid
                )
            )
        }
    }
}