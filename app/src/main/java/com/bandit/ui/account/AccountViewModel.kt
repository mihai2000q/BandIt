package com.bandit.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val _auth = DILocator.authenticator
    private val _database = DILocator.database
    private val _storage = DILocator.storage
    private val _account = MutableLiveData(_database.currentAccount)
    val account: LiveData<Account> = _account
    suspend fun updateAccount(
        name: String,
        nickname: String
    ) = coroutineScope {
        if(!_account.value!!.isEmpty())
            viewModelScope.launch {
                _database.updateAccount(
                    Account(
                        name = name,
                        nickname = nickname,
                        role = _account.value!!.role,
                        email = _account.value!!.email,
                        bandId = _account.value!!.bandId
                    )
                )
            }
    }

    suspend fun getProfilePicture(): ByteArray = coroutineScope {
        return@coroutineScope _storage.getProfilePicture(_auth.currentUser?.uid)
    }

    fun signOut() {
        _auth.signOut()
        _database.clearData()
    }

    companion object {
        const val TAG = Constants.Account.VIEW_MODEL_TAG
    }
}