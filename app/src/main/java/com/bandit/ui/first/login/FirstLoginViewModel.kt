package com.bandit.ui.first.login

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope

class FirstLoginViewModel : ViewModel() {
    private val _auth = DILocator.getAuthenticator()
    private val _database = DILocator.getDatabase()
    private val _storage = DILocator.getStorage()
    val name = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val role = MutableLiveData<BandItEnums.Account.Role>()
    suspend fun createAccount() = coroutineScope {
        _database.add(
            Account(
                name = name.value!!,
                nickname = nickname.value!!,
                role = role.value!!,
                bandId = null,
                bandName = null,
                email = _auth.currentUser?.email ?: "",
                userUid = _auth.currentUser?.uid
            )
        )
        _database.setUserAccountSetup(
            _auth.currentUser?.email ?: "",
            _auth.currentUser?.uid ?: "",
            true
        )
    }

    suspend fun saveProfilePicture(uri: Uri) = coroutineScope {
        _storage.setProfilePicture(_auth.currentUser?.uid, uri)
    }

    companion object {
        const val TAG = Constants.FirstLogin.VIEW_MODEL_TAG
    }
}