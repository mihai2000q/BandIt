package com.bandit.ui.account

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.data.repository.AccountRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val _auth = DILocator.getAuthenticator()
    private val _storage = DILocator.getStorage()
    private val _repository = AccountRepository(DILocator.getDatabase())
    private val _account = MutableLiveData(_repository.currentAccount)
    val account: LiveData<Account> = _account
    suspend fun createAccount(
        name: String,
        nickname: String,
        role: BandItEnums.Account.Role
    ) = coroutineScope {
        launch { _repository.createAccount(
            name, nickname, role, _auth
        ) }.join()
    }
    suspend fun updateAccount(
        name: String,
        nickname: String,
        role: BandItEnums.Account.Role
    ) = coroutineScope {
        launch { _repository.updateAccount(name, nickname, role) }.join()
        _account.value = _repository.currentAccount
    }

    suspend fun saveProfilePicture(imageUri: Uri) = coroutineScope {
        _storage.setProfilePicture(_auth.currentUser?.uid, imageUri)
    }

    suspend fun getProfilePicture(): ByteArray = coroutineScope {
        return@coroutineScope _storage.getProfilePicture(_auth.currentUser?.uid)
    }

    fun signOut() {
        _repository.signOut(_auth)
    }

    companion object {
        const val TAG = Constants.Account.VIEW_MODEL_TAG
    }
}