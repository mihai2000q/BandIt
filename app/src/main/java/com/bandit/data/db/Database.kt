package com.bandit.data.db

import com.bandit.constant.BandItEnums
import com.bandit.data.db.dto.BandInvitationDto
import com.bandit.data.model.*

interface Database {
    val homeNavigationElementsMap: Map<String, BandItEnums.Home.NavigationType>
    val concerts: List<Concert>
    val songs: List<Song>
    val albums: List<Album>
    val events: List<Event>
    val currentAccount: Account
    val currentBand: Band
    val currentBandInvitation: BandInvitation
    suspend fun init()
    suspend fun add(item: Any)
    suspend fun remove(item: Any)
    suspend fun edit(item: Any)
    suspend fun updateAccount(account: Account)
    suspend fun setUserAccountSetup(isAccountSetup: Boolean)
    suspend fun setBandInvitation(bandInvitationDto: BandInvitationDto)
    suspend fun sendBandInvitation(email: String)
    suspend fun acceptBandInvitation()
    suspend fun rejectBandInvitation()
    suspend fun isUserAccountSetup(): Boolean?
    fun clearData()
}