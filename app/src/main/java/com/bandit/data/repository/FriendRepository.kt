package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Account
import com.bandit.util.FilterUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FriendRepository(private val _database: Database? = null) {
    private val _people: MutableList<Account> = _database?.people?.toMutableList() ?: mutableListOf()
    val people: List<Account> = _people
    private val _friends: MutableList<Account> = _database?.friends?.toMutableList() ?: mutableListOf()
    val friends: List<Account> = _friends
    private val _friendRequests: MutableList<Account> = _database?.friendRequests?.toMutableList() ?: mutableListOf()
    val friendRequests: List<Account> = _friendRequests

    suspend fun sendFriendRequest(account: Account) = coroutineScope {
        async {
            launch { _database?.sendFriendRequest(account) }
            _people.remove(account)
        }
    }.await()

    suspend fun acceptFriendRequest(account: Account) = coroutineScope {
        async {
            launch { _database?.acceptFriendRequest(account) }
            _friendRequests.remove(account)
            _friends.add(account)
        }
    }.await()

    suspend fun rejectFriendRequest(account: Account) = coroutineScope {
        async {
            launch { _database?.rejectFriendRequest(account) }
            _friendRequests.remove(account)
            _people.add(account)
        }
    }.await()

    suspend fun unfriend(account: Account) = coroutineScope {
        async {
            launch { _database?.unfriend(account) }
            _friends.remove(account)
            _people.add(account)
        }
    }.await()

    fun removeBandForFriend(account: Account) {
        _friends.remove(account)
        account.bandId = null
        account.bandName = null
        _friends.add(account)
    }

    fun filterFriendRequests(name: String? = null): List<Account> =
        _friendRequests.filter { FilterUtils.filter(it.name, name) }

    fun filterFriends(name: String? = null): List<Account> =
        _friends.filter { FilterUtils.filter(it.name, name) }

    fun filterPeople(name: String? = null): List<Account> =
        _people.filter { FilterUtils.filter(it.name, name) }

}