package com.bandit.data.db

import android.util.Log
import com.bandit.data.db.entry.ConcertDBEntry
import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums
import com.bandit.mapper.Mappers
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseDatabase : Database {
    override val concerts: MutableList<Concert> = mutableListOf()
    override val homeNavigationElementsMap: Map<String, BandItEnums.Home.NavigationType> = mutableMapOf()
    private val _firestore = Firebase.firestore
    init {
        readConcerts()
    }
    override fun addConcert(concert: Concert) {
        _firestore.collection("Concerts")
            .add(Mappers.Concert.fromConcertToDBEntry(concert))
            .addOnFailureListener {
                Log.i("Firebase", "Concert ERROR $it")
            }
    }

    override fun removeConcert(concert: Concert) {
        _firestore.collection("Concerts")
            .get()
            .addOnSuccessListener {
                val result = it.filter { c -> c.get("id") as Long == concert.id.toLong() }
                if(result.size > 1)
                    throw RuntimeException("There can't be more concerts with the same ID")
                _firestore.collection("Concerts").document(result.first().id).delete()
            }
    }

    override fun editConcert(concert: Concert) {
        _firestore.collection("Concerts")
            .get()
            .addOnSuccessListener {
                val result = it.filter { c -> c.get("id") as Long == concert.id.toLong() }
                if(result.size > 1)
                    throw RuntimeException("There can't be more concerts with the same ID")
                _firestore.collection("Concerts").document(result.first().id).set(
                    Mappers.Concert.fromConcertToDBEntry(concert)
                )
            }
    }

    private fun readConcerts() {
        Firebase.firestore.collection("Concerts")
            .get()
            .addOnSuccessListener {
                for (result in it)
                    concerts.add(
                        Mappers.Concert.fromDBEntryToConcert(
                            ConcertDBEntry(
                                result.get("id") as Long,
                                result.get("name") as String,
                                result.get("dateTime") as String,
                                result.get("city") as String,
                                result.get("country") as String,
                                result.get("place") as String,
                                result.get("type") as Long,
                            )
                        )
                    )
            }
            .addOnFailureListener {
                Log.i("Firebase", "Concerts ERROR $it")
            }
        Log.i("Firebase", "Concerts imported successfully")
    }
}