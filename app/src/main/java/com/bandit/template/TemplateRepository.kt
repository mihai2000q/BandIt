package com.bandit.template

import com.bandit.data.db.Database

abstract class TemplateRepository<T>(
    private val _database: Database? = null,
    databaseList: List<T>?
)
where T : TemplateModel
{
    private val _list: MutableList<T> = databaseList?.toMutableList() ?: mutableListOf()
    val list: List<T> get() = _list
    suspend fun add(item: T) {
        val newItem = reassignId(item)
        _database?.add(newItem)
        _list.add(newItem)
    }
    suspend fun remove(item: T) {
        _database?.remove(item)
        _list.remove(item)
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