package com.bandit.data.db

import android.util.Log
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.db.entry.AccountDBEntry
import com.bandit.data.db.entry.BandDBEntry
import com.bandit.data.db.entry.ConcertDBEntry
import com.bandit.data.db.entry.AccountSetupDBEntry
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.data.model.BaseModel
import com.bandit.data.model.Concert
import com.bandit.di.DILocator
import com.bandit.mapper.AccountMapper
import com.bandit.mapper.BandMapper
import com.bandit.mapper.ConcertMapper
import com.bandit.mapper.Mapper
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FirebaseDatabase : Database {
    override val concerts: MutableList<Concert> = mutableListOf()
    override val homeNavigationElementsMap: MutableMap<String, BandItEnums.Home.NavigationType> = mutableMapOf()
    override val currentAccount: Account get() = if(::_currentAccount.isInitialized) _currentAccount else Account.EMPTY
    override val currentBand: Band get() = if(::_currentBand.isInitialized) _currentBand else Band.EMPTY
    private lateinit var _currentAccount: Account
    private lateinit var _currentBand: Band
    private val _firestore = Firebase.firestore

    override suspend fun init() {
        runBlocking {
            readHomeNavigationElements()
            readAccountAndBand()
            readConcerts()
        }
    }

    override suspend fun add(item: Any) {
        set(item)
    }

    override suspend fun remove(item: Any) {
        reset(item)
    }

    override suspend fun edit(item: Any) {
        set(item)
    }

    override suspend fun updateAccount(account: Account) {
        if(!::_currentAccount.isInitialized) return
        with(_currentAccount) {
            name = account.name
            nickname = account.nickname
            role = account.role
            bandId = account.bandId
            set(this)
        }
    }

    override suspend fun setUserAccountSetup(isAccountSetup: Boolean) {
        _firestore.collection(Constants.Firebase.Database.USER_ACCOUNT_SETUPS)
            .document(generateDocumentNameUserUid())
            .set(AccountSetupDBEntry(isAccountSetup, DILocator.authenticator.currentUser!!.uid))
            .await()
    }

    override suspend fun isUserAccountSetup(): Boolean? {
        return _firestore.collection(Constants.Firebase.Database.USER_ACCOUNT_SETUPS)
            .document(generateDocumentNameUserUid())
            .get()
            .await()
            .toObject(AccountSetupDBEntry::class.java)
            ?.accountSetup
    }

    override fun clearData() {
        concerts.clear()
        homeNavigationElementsMap.clear()
    }

    private suspend fun set(item: Any) {
        when(item) {
            is Account -> setItem(Constants.Firebase.Database.ACCOUNTS, AccountMapper.fromItemToDbEntry(item))
            is Band -> setItem(Constants.Firebase.Database.BANDS, BandMapper.fromItemToDbEntry(item))
            is Concert -> setItem(Constants.Firebase.Database.CONCERTS, ConcertMapper.fromItemToDbEntry(item))
        }
    }

    private suspend fun reset(item: Any) {
        when (item) {
            is Account -> deleteItem(Constants.Firebase.Database.ACCOUNTS, item)
            is Band -> deleteItem(Constants.Firebase.Database.BANDS, item)
            is Concert -> deleteItem(Constants.Firebase.Database.CONCERTS, item)
        }
    }

    private suspend fun setItem(table: String, item: BaseModel) = coroutineScope {
        async {
            _firestore.collection(table).document(generateDocumentNameId(table, item.id))
                .set(item)
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "${item.javaClass.name} ERROR $it")
                }
                .await()
        }
    }.await()

    private suspend fun deleteItem(table: String, item: BaseModel) = coroutineScope {
        async {
            _firestore.collection(table)
                .document(generateDocumentNameId(table, item.id))
                .delete()
                .addOnFailureListener {
                    Log.e(
                        Constants.Firebase.Database.TAG, "Error while selecting item of " +
                                "type ${item.javaClass.name} with error $it"
                    )
                }
                .await()
        }
    }.await()

    private suspend fun readConcerts() = coroutineScope {
        async {
            readItem(
                "Concerts",
                concerts,
                ConcertMapper,
                DILocator.authenticator.currentUser?.uid
            ) { result ->
                ConcertDBEntry(
                    result.get(Constants.Concert.Fields.id) as Long,
                    result.get(Constants.Concert.Fields.name) as String,
                    result.get(Constants.Concert.Fields.dateTime) as String,
                    result.get(Constants.Concert.Fields.city) as String,
                    result.get(Constants.Concert.Fields.country) as String,
                    result.get(Constants.Concert.Fields.place) as String,
                    result.get(Constants.Concert.Fields.type) as Long,
                    result.get(Constants.Concert.Fields.userUid) as String
                )
            }
        }
    }.await()

    private suspend fun <T, E> readItem(
        table: String,
        list: MutableList<T>,
        mapper: Mapper<T, E>,
        userUid: String?,
        action: (QueryDocumentSnapshot) -> E) = coroutineScope {
        async {
            _firestore.collection(table)
                .get()
                .addOnSuccessListener {
                    for (result in it)
                        if (!userUid.isNullOrEmpty()
                            && result.getString("userUid") != null
                            && userUid == result.getString("userUid")
                        )
                            list.add(mapper.fromDbEntryToItem(action(result)))
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "$table ERROR $it")
                }
                .await()
            Log.i(Constants.Firebase.Database.TAG, "$table imported successfully")
        }
    }.await()

    private suspend fun readAccountAndBand() = coroutineScope {
        var accountDBEntry: AccountDBEntry
        var bandDBEntry: BandDBEntry
        val accounts: MutableList<Account> = mutableListOf()
        async {

            val accountDBEntries = readAccountDbEntries {
                return@readAccountDbEntries it.userUid == DILocator.authenticator.currentUser?.uid
            }
            if(accountDBEntries.isEmpty()) return@async
            accountDBEntry = accountDBEntries.first()

            _currentAccount = AccountMapper.fromDbEntryToItem(accountDBEntry)

            val bandDBEntries = readBandDbEntries(accountDBEntry.id)
            if(bandDBEntries.isEmpty()) return@async
            bandDBEntry = bandDBEntries.first()

            val accountDbEntries = readAccountDbEntries {
                return@readAccountDbEntries it.bandId == bandDBEntry.id
            }

            accountDbEntries.forEach {
                accounts.add(AccountMapper.fromDbEntryToItem(it))
            }

            _currentBand = BandMapper.fromDbEntryToItem(bandDBEntry, accounts)

            Log.i(Constants.Firebase.Database.TAG, "Accounts imported successfully")
        }
    }.await()

    private suspend fun readAccountDbEntries(predicate: (account: AccountDBEntry) -> Boolean)
    : List<AccountDBEntry> = coroutineScope {
        async {
            val accountDbEntries = _firestore.collection(Constants.Firebase.Database.ACCOUNTS)
                .get()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Accounts ERROR $it")
                }
                .await()
                .toObjects<AccountDBEntry>()
                .filter(predicate)
            return@async accountDbEntries
        }
    }.await()

    private suspend fun readBandDbEntries(bandId: Long): List<BandDBEntry> = coroutineScope {
        async {
            val bandDBEntries = _firestore.collection(Constants.Firebase.Database.BANDS)
                .get()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Accounts ERROR $it")
                }
                .await()
                .toObjects<BandDBEntry>()
                .filter { it.id == bandId }

            if(bandDBEntries.size > 1)
                throw RuntimeException("there should be only one band linked to this account")
            return@async bandDBEntries
        }
    }.await()
    private fun readHomeNavigationElements() {
        //TODO: temporary, move it to database
        if(homeNavigationElementsMap.isEmpty())
            homeNavigationElementsMap.putAll(
                mapOf(
                    "Concerts" to BandItEnums.Home.NavigationType.Bottom,
                    "Songs" to BandItEnums.Home.NavigationType.Bottom,
                    "Chats" to BandItEnums.Home.NavigationType.Bottom,
                    "Schedule" to BandItEnums.Home.NavigationType.Bottom,
                )
            )
    }

    private fun generateDocumentNameUserUid() =
        Constants.Firebase.Database.USER_ACCOUNT_SETUPS.dropLast(1) + "-" +
                DILocator.authenticator.currentUser!!.uid

    private fun generateDocumentNameId(table: String, id: Long) =
        "${table.lowercase().dropLast(1)}$id"

}