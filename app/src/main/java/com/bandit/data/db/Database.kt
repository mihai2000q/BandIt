package com.bandit.data.db

import com.bandit.data.dto.BandInvitationDto
import com.bandit.data.model.*

/**
 * Database interface for accessing items
 */
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
    val bandInvitations: List<BandInvitation>
    val currentAccount: Account
    val currentBand: Band
    /**
     * This function is used to initialise the database by reading all
     * the items from the implemented database based on the current user Uid
     * @param userUid is the user unique identification
     */
    suspend fun init(userUid: String)
    /**
     * This method is used to add an item to the database
     * (if the data type is not supported, it will simply be ignored).
     * @param item any type of object
     *
     */
    suspend fun add(item: Any)
    /**
     * This method is used to remove an item from the database
     * (if the data type is not supported, it will simply be ignored).
     * @param item any type of object
     */
    suspend fun remove(item: Any)
    /**
     * This method is used to edit an item from the database
     * (if the data type is not supported, it will simply be ignored).
     * @param item any type of object
     */
    suspend fun edit(item: Any)
    suspend fun updateAccount(account: Account)
    suspend fun setUserAccountSetup(userUid: String, email: String, isAccountSetup: Boolean)
    suspend fun isUserAccountSetup(userUid: String): Boolean?
    suspend fun createBand(name: String)
    suspend fun setBandInvitation(bandInvitationDto: BandInvitationDto)
    suspend fun sendBandInvitation(account: Account)
    suspend fun acceptBandInvitation(bandInvitation: BandInvitation)
    suspend fun rejectBandInvitation(bandInvitation: BandInvitation)
    suspend fun sendFriendRequest(account: Account)
    suspend fun acceptFriendRequest(account: Account)
    suspend fun rejectFriendRequest(account: Account)
    /**
     * This method verifies whether the given email is in use or not.
     * @param email the email that is checked
     * @return the result of the checking
     */
    suspend fun isEmailInUse(email: String): Boolean
    /**
     * This method verifies if the database is available.
     * @return the result of the checking
     */
    suspend fun isConnected(): Boolean
    /**
     * This method clears all the data.
     */
    fun clearData()
}