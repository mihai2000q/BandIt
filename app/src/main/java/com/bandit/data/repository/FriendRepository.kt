package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Account
import com.bandit.util.FilterUtils

class FriendRepository(private val _database: Database? = null) {
    private val _people: MutableList<Account> = _database?.people?.toMutableList() ?: mutableListOf()
    val people: List<Account> = _people
    private val _friends: MutableList<Account> = _database?.friends?.toMutableList() ?: mutableListOf()
    val friends: List<Account> = _friends
    private val _friendRequests: MutableList<Account> = _database?.friendRequests?.toMutableList() ?: mutableListOf()
    val friendRequests: List<Account> = _friendRequests

    suspend fun sendFriendRequest(account: Account) {
        _database?.sendFriendRequest(account)
        _people.remove(account)
    }

    suspend fun acceptFriendRequest(account: Account) {
        _database?.acceptFriendRequest(account)
        _friendRequests.remove(account)
        _friends.add(account)
    }

    suspend fun rejectFriendRequest(account: Account) {
        _database?.rejectFriendRequest(account)
        _friendRequests.remove(account)
        _people.add(account)
    }

    fun filterFriendRequests(name: String? = null): List<Account> =
        _friendRequests.filter { FilterUtils.filter(it.name, name) }

    fun filterFriends(name: String? = null): List<Account> =
        _friends.filter { FilterUtils.filter(it.name, name) }

    fun filterPeople(name: String? = null): List<Account> =
        _people.filter { FilterUtils.filter(it.name, name) }

}