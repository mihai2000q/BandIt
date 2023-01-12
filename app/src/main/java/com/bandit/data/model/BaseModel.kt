package com.bandit.data.model

open class BaseModel(private val _id: Int) {
    val id get() = _id
}