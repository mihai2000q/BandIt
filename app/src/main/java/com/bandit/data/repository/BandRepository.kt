package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Account
import com.bandit.data.model.BandInvitation

class BandRepository(val database: Database? = null) {
    private val _bandInvitations: MutableList<BandInvitation> = database?.bandInvitations?.toMutableList() ?: mutableListOf()
    val bandInvitations: List<BandInvitation> = _bandInvitations

    suspend fun createBand(name: String) {
        database?.createBand(name)
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
}