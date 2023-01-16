package com.bandit.data.db

import android.util.Log
import com.bandit.data.db.entry.ConcertDBEntry
import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.BaseModel
import com.bandit.mapper.Mappers
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FirebaseDatabase : Database {
    override val concerts: MutableList<Concert> = mutableListOf()
    override val homeNavigationElementsMap: MutableMap<String, BandItEnums.Home.NavigationType> = mutableMapOf()
    private val _firestore = Firebase.firestore

    override suspend fun init() {
        readHomeNavigationElements()
        readConcerts()
    }

    override fun addConcert(concert: Concert) {
        addItem("Concerts", Mappers.Concert.fromConcertToDBEntry(concert))
    }

    override fun removeConcert(concert: Concert) {
        selectItemById("Concerts", concert) { it.delete() }
    }

    override fun editConcert(concert: Concert) {
        selectItemById("Concerts", concert) {
            it.set(Mappers.Concert.fromConcertToDBEntry(concert))
        }
    }

    private fun addItem(table: String, item: Any) {
        _firestore.collection(table)
            .add(item)
            .addOnFailureListener {
                Log.e(Constants.Firebase.DATABASE_TAG, "${item.javaClass.name} ERROR $it")
            }
    }

    private fun selectItemById(table: String, item: BaseModel, action: (DocumentReference) -> Unit) {
        _firestore.collection(table)
            .get()
            .addOnSuccessListener {
                val result = it.filter { c -> c.get("id") as Long == item.id.toLong() }
                if(result.size > 1)
                    throw RuntimeException("There can't be more items " +
                            "of type ${item.javaClass.name} with the same ID")
                val doc = _firestore.collection(table).document(result.first().id)
                action(doc)
            }
            .addOnFailureListener{
                Log.e(Constants.Firebase.DATABASE_TAG, "Error while selecting item of " +
                        "type ${item.javaClass.name} with error $it")
            }
    }

    private suspend fun readConcerts() = coroutineScope {
        async {
            Firebase.firestore.collection("Concerts")
                .get()
                .addOnSuccessListener {
                    for (result in it)
                        concerts.add(
                            Mappers.Concert.fromDBEntryToConcert(
                                ConcertDBEntry(
                                    result.get(Constants.Concert.Fields.id) as Long,
                                    result.get(Constants.Concert.Fields.name) as String,
                                    result.get(Constants.Concert.Fields.dateTime) as String,
                                    result.get(Constants.Concert.Fields.city) as String,
                                    result.get(Constants.Concert.Fields.country) as String,
                                    result.get(Constants.Concert.Fields.place) as String,
                                    result.get(Constants.Concert.Fields.type) as Long,
                                )
                            )
                        )
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.DATABASE_TAG, "Concerts ERROR $it")
                }
            Log.i(Constants.Firebase.DATABASE_TAG, "Concerts imported successfully")
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
}