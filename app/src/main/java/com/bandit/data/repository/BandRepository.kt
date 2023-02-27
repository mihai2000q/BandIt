package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.data.model.BandInvitation
import com.bandit.util.FilterUtils

class BandRepository(val database: Database? = null) {
    private val _bandInvitations: MutableList<BandInvitation> = database?.bandInvitations?.toMutableList() ?: mutableListOf()
    val bandInvitations: List<BandInvitation> = _bandInvitations

    suspend fun createBand(name: String) {
        database?.createBand(
            Band(
                name = name,
                creator = database.currentAccount.id,
                mutableMapOf()
            )
        )
        _bandInvitations.clear()
    }

    suspend fun sendBandInvitation(account: Account) {
        database?.sendBandInvitation(account)
    }

    suspend fun acceptBandInvitation(bandInvitation: BandInvitation) {
        database?.acceptBandInvitation(bandInvitation)
        _bandInvitations.clear()
    }

    suspend fun rejectBandInvitation(bandInvitation: BandInvitation) {
        database?.rejectBandInvitation(bandInvitation)
        _bandInvitations.remove(bandInvitation)
    }

    fun filterBandInvitations(bandName: String?) : List<BandInvitation> =
        _bandInvitations.filter { FilterUtils.filter(it.band.name, bandName) }
}