package com.bandit.data.repository

import com.bandit.auth.Authenticator
import com.bandit.constant.BandItEnums
import com.bandit.data.db.Database
import com.bandit.data.dto.UserAccountDto
import com.bandit.data.model.Account
import kotlinx.coroutines.coroutineScope

class AccountRepository(private val _database: Database? = null) {
    private var _currentAccount = _database?.currentAccount ?: Account.EMPTY
    val currentAccount get() = _currentAccount

    suspend fun createAccount(
        name: String,
        nickname: String,
        role: BandItEnums.Account.Role,
        auth: Authenticator
    ) = coroutineScope {
        val newAccount = Account(
            name = name,
            nickname = nickname,
            role = role,
            bandId = null,
            bandName = null,
            email = auth.currentUser?.email ?: "",
            userUid = auth.currentUser?.uid
        )
        _database?.add(newAccount)
        if(_database != null)
            setUserAccountSetup(
                _database,
                auth,
                true
            )
        _currentAccount = newAccount
    }

    suspend fun updateAccount(
        name: String,
        nickname: String,
        role: BandItEnums.Account.Role
    ) = coroutineScope {
        val newAccount = Account(
            name = name,
            nickname = nickname,
            role = role,
            email = _currentAccount.email,
            bandId = _currentAccount.bandId,
            bandName = _currentAccount.bandName,
            id = _currentAccount.id,
            userUid = _currentAccount.userUid
        )
        _database?.edit(newAccount)
        _currentAccount = newAccount
    }

    fun signOut(auth: Authenticator) {
        _database?.clearData()
        auth.signOut()
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