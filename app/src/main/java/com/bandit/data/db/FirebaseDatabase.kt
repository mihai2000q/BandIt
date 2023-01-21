package com.bandit.data.db

import android.util.Log
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.db.entry.ConcertDBEntry
import com.bandit.data.model.BaseModel
import com.bandit.data.model.Concert
import com.bandit.di.DILocator
import com.bandit.mapper.AccountMapper
import com.bandit.mapper.ConcertMapper
import com.bandit.mapper.Mapper
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FirebaseDatabase : Database {
    override val concerts: MutableList<Concert> = mutableListOf()
    override val homeNavigationElementsMap: MutableMap<String, BandItEnums.Home.NavigationType> = mutableMapOf()
    private val _firestore = Firebase.firestore

    override suspend fun init() {
        runBlocking {
            readHomeNavigationElements()
            readConcerts()
            readAccounts()
        }
    }

    override suspend fun add(item: Any) {
        when(item) {
            is Concert -> setItem("Concerts", ConcertMapper.fromItemToDbEntry(item))
        }
    }

    override suspend fun remove(item: Any) {
        when (item) {
            is Concert -> deleteItem("Concerts", item)
        }
    }

    override suspend fun edit(item: Any) {
        when (item) {
            is Concert -> setItem("Concerts", item)
        }
    }

    override fun clearData() {
        concerts.clear()
        homeNavigationElementsMap.clear()
    }

    private suspend fun setItem(table: String, item: BaseModel) = coroutineScope {
        async {
            _firestore.collection(table).document(generateDocumentName(table, item.id))
                .set(item)
                .addOnFailureListener {
                    Log.e(Constants.Firebase.DATABASE_TAG, "${item.javaClass.name} ERROR $it")
                }.await()
        }
    }.await()

    private suspend fun deleteItem(table: String,
                                       item: BaseModel) = coroutineScope {
        async {
            _firestore.collection(table)
                .document(generateDocumentName(table, item.id))
                .delete()
                .addOnFailureListener {
                    Log.e(
                        Constants.Firebase.DATABASE_TAG, "Error while selecting item of " +
                                "type ${item.javaClass.name} with error $it"
                    )
                }.await()
        }
    }.await()

    private suspend fun readConcerts() = coroutineScope {
        async {
            readItem(
                "Concerts",
                concerts,
                ConcertMapper,
                DILocator.authenticator.currentUser?.uid
            ) { result ->
                ConcertDBEntry(
                    result.get(Constants.Concert.Fields.id) as Long,
                    result.get(Constants.Concert.Fields.name) as String,
                    result.get(Constants.Concert.Fields.dateTime) as String,
                    result.get(Constants.Concert.Fields.city) as String,
                    result.get(Constants.Concert.Fields.country) as String,
                    result.get(Constants.Concert.Fields.place) as String,
                    result.get(Constants.Concert.Fields.type) as Long,
                    result.get(Constants.Concert.Fields.userUid) as String
                )
            }
        }
    }.await()

    private suspend fun <T, E> readItem(
        table: String,
        list: MutableList<T>,
        mapper: Mapper<T, E>,
        userUid: String?,
        action: (QueryDocumentSnapshot) -> E)
    = coroutineScope {
        async {
            Firebase.firestore.collection(table)
                .get()
                .addOnSuccessListener {
                    for (result in it)
                        if (!userUid.isNullOrEmpty()
                            && result.getString("userUid") != null
                            && userUid == result.getString("userUid")
                        )
                            list.add(mapper.fromDbEntryToItem(action(result)))
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.DATABASE_TAG, "Concerts ERROR $it")
                }.await()
            Log.i(Constants.Firebase.DATABASE_TAG, "$table imported successfully")
        }
    }.await()

    private fun readHomeNavigationElements() {
        //TODO: temporary, move it to database
        if(homeNavigationElementsMap.isEmpty())
            homeNavigationElementsMap.putAll(
                mapOf(
                    "Concerts" to BandItEnums.Home.NavigationType.Bottom,
                    "Songs" to BandItEnums.Home.NavigationType.Bottom,
                    "Chats" to BandItEnums.Home.NavigationType.Bottom,
                    "Schedule" to BandItEnums.Home.NavigationType.Bottom,
                )
            )
    }

    private fun generateDocumentName(table: String, id: Long): String {
        return "${table.lowercase().dropLast(1)}$id"
    }

}