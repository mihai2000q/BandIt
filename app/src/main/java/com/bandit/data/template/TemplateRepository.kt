package com.bandit.data.template

import com.bandit.data.db.Database
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

abstract class TemplateRepository<T>(
    private val _database: Database? = null,
    databaseList: List<T>?
)
where T : Item
{
    private val _list: MutableList<T> = databaseList?.toMutableList() ?: mutableListOf()
    val list: List<T> get() = _list
    suspend fun add(item: T) = coroutineScope {
        async {
            val newItem = reassignId(item)
            launch { _database?.add(newItem) }
            _list.add(newItem)
        }
    }.await()
    suspend fun remove(item: T) = coroutineScope {
        async {
            launch { _database?.remove(item) }
            _list.remove(item)
        }
    }.await()
    suspend fun edit(item: T) = coroutineScope {
        async {
            _list.asSequence()
                .filter { it.id == item.id }
                .forEach { _list[_list.indexOf(it)] = item }
            launch { _database?.edit(item) }
        }
    }.await()
    protected abstract fun reassignId(item: T): T
    protected fun isIdUsed(id: Long): Boolean {
        return !_list.none { it.id == id }
    }
}