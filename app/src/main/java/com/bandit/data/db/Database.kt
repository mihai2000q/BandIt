package com.bandit.data.db

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
    /**
     * This method is used to check whether a user has setup their account yet
     * @param userUid the user that is checked
     * @return result of the query
     */
    suspend fun isUserAccountSetup(userUid: String): Boolean?
    /**
     * This method creates the given Band object
     * and updates all the components linked to it
     * @param band the given band input
     */
    suspend fun createBand(band: Band)
    /**
     * This method is used to send a band invitation to an user
     * @param account is the user account which receives the invitation
     */
    suspend fun sendBandInvitation(account: Account)
    /**
     * This method is used to accept a band invitation
     * @param bandInvitation is the invitation
     */
    suspend fun acceptBandInvitation(bandInvitation: BandInvitation)
    /**
     * This method is used to reject a band invitation
     * @param bandInvitation is the invitation
     */
    suspend fun rejectBandInvitation(bandInvitation: BandInvitation)
    /**
     * This method is used to kick a band member
     * @param account the user that is kicked
     */
    suspend fun kickBandMember(account: Account)
    /**
     * This method is used to abandon a band
     */
    suspend fun abandonBand()
    /**
     * This method is used to send a friend request to another user
     * @param account is the account which receives the request
     */
    suspend fun sendFriendRequest(account: Account)
    /**
     * This method is used to accept a friend request from a user
     * @param account is the account which becomes your friend
     */
    suspend fun acceptFriendRequest(account: Account)
    /**
     * This method is used to reject a friend request from a user
     * @param account is the account which you reject
     */
    suspend fun rejectFriendRequest(account: Account)
    /**
     * This method is used to unfriend a friend
     * @param account is the account which you unfriend
     */
    suspend fun unfriend(account: Account)
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