package com.bandit.data.db

import android.util.Log
import com.bandit.data.db.entry.ConcertDBEntry
import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums
import com.bandit.helper.Mapper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime

class FirebaseDatabase : Database {
    override val concerts: MutableList<Concert> = mutableListOf()
    override val homeNavigationElementsMap: Map<String, BandItEnums.Home.NavigationType> = mutableMapOf()
    private val _firestore = Firebase.firestore
    init {
        readConcerts()
    }
    override fun addConcert(concert: Concert): Boolean {
        var result = true
        _firestore.collection("Concerts")
            .add(
                ConcertDBEntry(
                    concert.id,
                    concert.name,
                    concert.dateTime.toString(),
                    concert.city,
                    concert.country,
                    concert.place,
                    Mapper.Concert.mapConcertTypeToInt(concert.type)
                )
            )
            .addOnSuccessListener {
                result = true
            }
            .addOnFailureListener {
                Log.i("Firebase", "Concert ERROR $it")
                result = false
            }
        return result
    }

    override fun removeConcert(concert: Concert): Boolean {
        TODO("Not yet implemented")
    }

    override fun editConcert(concert: Concert): Boolean {
        TODO("Not yet implemented")
    }

    private fun readConcerts() {
        Firebase.firestore.collection("Concerts")
            .get()
            .addOnSuccessListener {
                for (result in it)
                    concerts.add(
                        Concert(
                            result.get("name") as String,
                            LocalDateTime.parse(result.get("dateTime") as String),
                            result.get("city") as String,
                            result.get("country") as String,
                            result.get("place") as String,
                            Mapper.Concert.mapIntToConcertType((result.get("type") as Long).toInt()),
                            (result.get("id") as Long).toInt()
                        )
                    )
            }
            .addOnFailureListener {
                Log.i("Firebase", "Concerts ERROR $it")
            }
        Log.i("Firebase", "Concerts imported successfully")
    }
}