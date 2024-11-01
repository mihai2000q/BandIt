package com.bandit.data.repository

import com.bandit.auth.Authenticator
import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.dto.UserAccountDto
import com.bandit.data.model.Account
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AccountRepository(private val _database: Database? = null) {
    private var _currentAccount = _database?.currentAccount ?: Account.EMPTY
    val currentAccount get() = _currentAccount

    suspend fun createAccount(
        name: String,
        nickname: String,
        role: BandItEnums.Account.Role,
        auth: Authenticator?
    ) = coroutineScope {
        val newAccount = Account(
            name = name,
            nickname = nickname,
            role = role,
            bandId = null,
            bandName = null,
            email = auth?.currentUser?.email ?: "",
            userUid = auth?.currentUser?.uid
        )
        if(_database == null)
            _currentAccount = newAccount
        _database?.add(newAccount)
        if (_database != null && auth != null)
            launch {
                setUserAccountSetup(
                    _database,
                    auth,
                    true
                )
            }
    }

    suspend fun updateAccount(newAccount: Account) = coroutineScope {
        _database?.edit(newAccount)
        _currentAccount = newAccount
    }

    fun signOut(auth: Authenticator?) {
        if(_database == null)
            _currentAccount = Account.EMPTY
        _database?.clearData()
        auth?.signOut()
    }

    companion object {
        suspend fun setUserAccountSetup(database: Database, auth: Authenticator, accountSetup: Boolean) {
            database.add(
                UserAccountDto(
                    userUid = auth.currentUser?.uid ?: "",
                    email = auth.currentUser?.email ?: "",
                    accountSetup = accountSetup
                )
            )
        }
        suspend fun isUserAccountSetup(database: Database, userUid: String) =
            database.isUserAccountSetup(userUid)

    }
}