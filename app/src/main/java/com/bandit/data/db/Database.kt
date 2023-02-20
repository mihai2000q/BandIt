package com.bandit.data.db

import com.bandit.data.db.dto.BandInvitationDto
import com.bandit.data.model.*

interface Database {
    val concerts: List<Concert>
    val songs: List<Song>
    val albums: List<Album>
    val events: List<Event>
    val tasks: List<Task>
    val notes: List<Note>
    val people: List<Account>
    val friends: List<Account>
    val friendRequests: List<Account>
    val currentAccount: Account
    val currentBand: Band
    val currentBandInvitation: BandInvitation
    suspend fun init()
    suspend fun add(item: Any)
    suspend fun remove(item: Any)
    suspend fun edit(item: Any)
    suspend fun updateAccount(account: Account)
    suspend fun setUserAccountSetup(isAccountSetup: Boolean)
    suspend fun isUserAccountSetup(): Boolean?
    suspend fun setBandInvitation(bandInvitationDto: BandInvitationDto)
    suspend fun sendBandInvitation(account: Account)
    suspend fun acceptBandInvitation()
    suspend fun rejectBandInvitation()
    suspend fun sendFriendRequest(account: Account)
    suspend fun acceptFriendRequest(account: Account)
    suspend fun rejectFriendRequest(account: Account)
    suspend fun isEmailInUse(email: String): Boolean
    suspend fun isConnected(): Boolean
    fun clearData()
}