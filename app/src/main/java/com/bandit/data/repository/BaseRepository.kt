package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.BaseModel

abstract class BaseRepository<T>(
    private val _database: Database? = null,
    databaseList: List<T>?
)
where T : BaseModel
{
    private val _list: MutableList<T> = mutableListOf()
    val list: List<T> get() = _list
    init {
        _list.addAll(databaseList ?: listOf())
    }
    suspend fun add(item: T) {
        val newItem = reassignId(item)
        _database?.add(newItem)
        _list.add(newItem)
    }
    suspend fun remove(item: T): Boolean {
        _database?.remove(item)
        if(!_list.contains(item)) return false
        _list.remove(item)
        return true
    }
    suspend fun edit(item: T) {
        _database?.edit(item)
        _list
            .asSequence()
            .filter { it.id == item.id }
            .forEach { _list[_list.indexOf(it)] = item }
    }
    protected abstract fun reassignId(item: T): T
    protected fun isIdUsed(id: Long): Boolean {
        return !_list.none { it.id == id }
    }
}