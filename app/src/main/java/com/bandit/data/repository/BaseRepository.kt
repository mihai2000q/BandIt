package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.BaseModel
import java.util.*

abstract class BaseRepository<T>(
    private val _database: Database? = null,
    databaseList: List<T>?
)
where T : BaseModel
{
    private val _list: MutableList<T> = databaseList?.toMutableList() ?: mutableListOf()
    val list: List<T> get() = _list
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
    protected fun <E> filter(obj: E, other: E) =
        when(obj) {
            is String -> filterString(obj, other as String?)
            else -> filterObjects(obj, other)
        }
    private fun filterString(string: String, other: String?) =
        if(other != null) {
            if(other.split(" ").size > 1)
                filterOneString(string, other)
            else
                filterMultipleStrings(string, other)
        } else true
    private fun <E> filterObjects(obj: E, other: E) =
        if(other == null)
            true
        else Objects.equals(obj, other)
    private fun filterMultipleStrings(string: String, other: String?): Boolean {
        string.split(" ").forEach {
            if(filterOneString(it, other))
                return true
        }
        return false
    }
    private fun filterOneString(string: String, other: String?) =
        string.lowercase().startsWith(other?.lowercase() ?: "")
}