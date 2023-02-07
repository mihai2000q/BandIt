package com.bandit.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val _database = DILocator.database
    private val _account = MutableLiveData(_database.currentAccount)
    val account: LiveData<Account> = _account
    suspend fun updateAccount(
        name: String,
        nickname: String
    ) {
        if(!_account.value!!.isEmpty())
            viewModelScope.launch {
                _database.updateAccount(
                    Account(
                        name,
                        nickname,
                        _account.value!!.role,
                        _account.value!!.bandId,
                        _account.value!!.email
                    )
                )
            }
    }

    companion object {
        const val TAG = Constants.Account.VIEW_MODEL_TAG
    }
}