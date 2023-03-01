package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.data.model.BandInvitation
import com.bandit.util.FilterUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class BandRepository(private val _database: Database? = null) {
    private val _bandInvitations: MutableList<BandInvitation> = _database?.bandInvitations?.toMutableList() ?: mutableListOf()
    val bandInvitations: List<BandInvitation> = _bandInvitations
    private var _band = _database?.currentBand ?: Band.EMPTY
    val band get() = _band

    suspend fun createBand(band: Band) {
        _database?.createBand(band)
        _band = band
        _bandInvitations.clear()
    }

    suspend fun sendBandInvitation(account: Account) {
        _database?.sendBandInvitation(account)
    }

    suspend fun acceptBandInvitation(bandInvitation: BandInvitation) {
        _database?.acceptBandInvitation(bandInvitation)
        _bandInvitations.clear()
    }

    suspend fun rejectBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        _database?.rejectBandInvitation(bandInvitation)
        _bandInvitations.remove(bandInvitation)
    }

    suspend fun kickBandMember(account: Account) = coroutineScope {
        async {
            _band.members.remove(account)
            launch { _database?.kickBandMember(account) }
        }
    }.await()

    suspend fun abandonBand() = coroutineScope {
        async {
            launch { _database?.abandonBand() }
            _band = Band.EMPTY
        }
    }.await()

    suspend fun disbandBand() = coroutineScope {
        async {
            launch { _database?.disbandBand() }
            _band = Band.EMPTY
        }
    }.await()

    fun filterBandInvitations(bandName: String?) : List<BandInvitation> =
        _bandInvitations.filter { FilterUtils.filter(it.band.name, bandName) }
}