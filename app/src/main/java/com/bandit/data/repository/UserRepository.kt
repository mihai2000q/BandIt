package com.bandit.data.repository

import com.bandit.data.model.User

class UserRepository : BaseRepository() {
    private val _users: MutableList<User> = mutableListOf()
    val users: List<User> get() = _users
    init {
        //_users.addAll(_database?.concerts ?: listOf())
    }
}