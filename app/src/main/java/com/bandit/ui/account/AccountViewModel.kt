package com.bandit.ui.account

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope

class AccountViewModel : ViewModel() {
    private val _auth = DILocator.getAuthenticator()
    private val _database = DILocator.getDatabase()
    private val _storage = DILocator.getStorage()
    private val _account = MutableLiveData(_database.currentAccount)
    val account: LiveData<Account> = _account
    suspend fun updateAccount(
        name: String,
        nickname: String,
        role: BandItEnums.Account.Role
    ) = coroutineScope {
        with(_account.value!!) {
            if (!this.isEmpty()) {
                val newAccount = Account(
                    name = name,
                    nickname = nickname,
                    role = role,
                    email = this.email,
                    bandId = this.bandId,
                    bandName = this.bandName,
                    id = this.id,
                    userUid = this.userUid
                )
                _database.edit(newAccount)
                _account.value = newAccount
            }
        }
    }

    suspend fun updateProfilePicture(imageUri: Uri) = coroutineScope {
        _storage.setProfilePicture(_auth.currentUser?.uid, imageUri)
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