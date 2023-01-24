package com.bandit.data.db

import com.bandit.data.model.Concert
import com.bandit.constant.BandItEnums
import com.bandit.data.db.entry.BandInvitationDBEntry
import com.bandit.data.model.Account
import com.bandit.data.model.Band

interface Database {
    val homeNavigationElementsMap: Map<String, BandItEnums.Home.NavigationType>
    val concerts: List<Concert>
    val currentAccount: Account
    val currentBand: Band
    suspend fun init()
    suspend fun add(item: Any)
    suspend fun remove(item: Any)
    suspend fun edit(item: Any)
    suspend fun updateAccount(account: Account)
    suspend fun setUserAccountSetup(isAccountSetup: Boolean)
    suspend fun setBandInvitationDBEntry(bandInvitationDBEntry: BandInvitationDBEntry)
    suspend fun isUserAccountSetup(): Boolean?
    fun clearData()
}