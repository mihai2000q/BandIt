package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Account

class FriendRepository(val database: Database? = null) {
    private val _people: MutableList<Account> = database?.people?.toMutableList() ?: mutableListOf()
    val people: List<Account> = _people
    private val _friends: MutableList<Account> = database?.friends?.toMutableList() ?: mutableListOf()
    val friends: List<Account> = _friends
    private val _friendRequests: MutableList<Account> = database?.friends?.toMutableList() ?: mutableListOf()
    val friendRequests: List<Account> = _friendRequests

    suspend fun sendFriendRequest(account: Account) {
        database?.sendFriendRequest(account)
        _friendRequests.add(account)
        _people.remove(account)
    }

    suspend fun acceptFriendRequest(account: Account) {
        database?.sendFriendRequest(account)
        _friendRequests.remove(account)
        _friends.add(account)
    }

    suspend fun rejectFriendRequest(account: Account) {
        database?.sendFriendRequest(account)
        _friendRequests.remove(account)
        _people.add(account)
    }
}