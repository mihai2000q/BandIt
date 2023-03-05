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

    suspend fun createBand(band: Band) = coroutineScope {
        async {
            _database?.createBand(band)
            _band = _database?.currentBand ?: band
            _bandInvitations.clear()
        }
    }.await()

    suspend fun sendBandInvitation(account: Account) = coroutineScope {
        async {
            _database?.sendBandInvitation(account)
        }
    }.await()

    suspend fun acceptBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        async {
            _database?.acceptBandInvitation(bandInvitation)
            _band = _database?.currentBand ?: bandInvitation.band
            _bandInvitations.clear()
        }
    }.await()

    suspend fun rejectBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        async {
            _bandInvitations.remove(bandInvitation)
            launch { _database?.rejectBandInvitation(bandInvitation) }
        }
    }.await()

    suspend fun kickBandMember(account: Account) = coroutineScope {
        async {
            if(_database == null) {
                _band.members.remove(account)
                account.bandId = null
                account.bandName = null
            }
            _database?.kickBandMember(account)
        }
    }.await()

    suspend fun abandonBand() = coroutineScope {
        async {
            _database?.abandonBand()
            _band = _database?.currentBand ?: Band.EMPTY
        }
    }.await()

    suspend fun disbandBand() = coroutineScope {
        async {
            _database?.disbandBand()
            _band = _database?.currentBand ?: Band.EMPTY
        }
    }.await()

    fun filterBandInvitations(bandName: String?) : List<BandInvitation> =
        _bandInvitations.filter { FilterUtils.filter(it.band.name, bandName) }
}