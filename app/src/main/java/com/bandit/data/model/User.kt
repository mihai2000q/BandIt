package com.bandit.data.model

import com.bandit.util.AndroidUtils

data class User(
    val username: String,
    val password: String,
    val settings: Settings,
    private val _id: Int = AndroidUtils.generateRandomId()
    ) {
    val id get() = _id

    override fun toString(): String {
        return "User(id=$id, username='$username', password='$password')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (username != other.username) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }

}
