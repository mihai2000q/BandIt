package com.bandit.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.data.model.Account
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name
    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname
    private val _role = MutableLiveData<String>()
    val role: LiveData<String> = _role

    private val _database = DILocator.database
    private val _currentAccount = _database.currentAccount
    init {
        if(!_currentAccount.isEmpty()) {
            _name.value = _currentAccount.name
            _nickname.value = _currentAccount.nickname
            _role.value = _currentAccount.role.name
        }
    }
    fun updateAccount(
        name: String,
        nickname: String
    ) {
        if(!_currentAccount.isEmpty())
            viewModelScope.launch {
                _database.updateAccount(
                    Account(
                        name,
                        nickname,
                        _currentAccount.role,
                        null
                    )
                )
            }
    }
}