package com.bandit.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.model.Account
import com.bandit.data.repository.AccountRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AccountRepositoryTest {
    private lateinit var accountRepository: AccountRepository
    @Before
    fun setup() {
        accountRepository = AccountRepository()
        this.createAccount()
    }
    @Test
    fun account_repository_create_account() {
        // created and checked in setup
    }
    @Test
    fun account_repository_update_account() {
        val newName = "Newish Account-ish"
        val newNickname = "Newer"
        val newRole = BandItEnums.Account.Role.Manager
        runBlocking { accountRepository.updateAccount(newName, newNickname, newRole) }
        assertEquals(newName, accountRepository.currentAccount.name)
        assertEquals(newNickname, accountRepository.currentAccount.nickname)
        assertEquals(newRole, accountRepository.currentAccount.role)
    }
    @Test
    fun account_repository_sign_out() {
        runBlocking { accountRepository.signOut(null) }
        assertEquals(Account.EMPTY, accountRepository.currentAccount)
    }
    private fun createAccount() {
        val name = "New Account"
        val nickname = "Newy"
        val role = BandItEnums.Account.Role.RhythmGuitar
        assertEquals(Account.EMPTY, accountRepository.currentAccount)
        runBlocking { accountRepository.createAccount(name, nickname, role, null) }
        assertEquals(name, accountRepository.currentAccount.name)
        assertEquals(nickname, accountRepository.currentAccount.nickname)
        assertEquals(role, accountRepository.currentAccount.role)
    }
}