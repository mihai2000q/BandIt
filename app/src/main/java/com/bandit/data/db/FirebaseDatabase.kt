package com.bandit.data.db

import android.util.Log
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.db.dto.*
import com.bandit.data.model.*
import com.bandit.di.DILocator
import com.bandit.mapper.*
import com.bandit.util.AndroidUtils
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
    override val currentAccount: Account get() = _currentAccount
    override val currentBand: Band get() = _currentBand
    override val currentBandInvitation: BandInvitation get() = _currentBandInvitation
    private var _currentAccount = Account.EMPTY
    private var _currentBand = Band.EMPTY
    private var _currentBandInvitation = BandInvitation.EMPTY
    private val _firestore = Firebase.firestore

    override suspend fun init() {
        runBlocking {
            readHomeNavigationElements()
            readAccount()
            readBand()
            readBandInvitation()
            if(_currentBand.isEmpty()) return@runBlocking
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
        if(!_currentAccount.isEmpty()) return
        with(_currentAccount) {
            name = account.name
            nickname = account.nickname
            role = account.role
            bandId = account.bandId
            set(this)
        }
    }

    override suspend fun setUserAccountSetup(isAccountSetup: Boolean) = coroutineScope {
        async {
            _firestore.collection(Constants.Firebase.Database.USER_ACCOUNT_SETUPS)
                .document(generateDocumentNameUserUid())
                .set(AccountSetupDto(isAccountSetup, DILocator.authenticator.currentUser!!.uid))
                .await()
            return@async
        }
    }.await()

    override suspend fun isUserAccountSetup(): Boolean? = coroutineScope {
        async {
            return@async _firestore.collection(Constants.Firebase.Database.USER_ACCOUNT_SETUPS)
                .document(generateDocumentNameUserUid())
                .get()
                .await()
                .toObject(AccountSetupDto::class.java)
                ?.accountSetup
        }
    }.await()

    override suspend fun setBandInvitation(bandInvitationDto: BandInvitationDto) = coroutineScope {
        async {
            _firestore.collection(Constants.Firebase.Database.BAND_INVITATIONS)
                .document(
                    generateDocumentNameId(
                        Constants.Firebase.Database.BAND_INVITATIONS,
                        bandInvitationDto.id
                    )
                )
                .set(bandInvitationDto)
                .await()
            return@async
        }
    }.await()

    override suspend fun sendBandInvitation(email: String) = coroutineScope {
        async {
            val accountDtos = readAccountDtos { it.email == email }
            if (accountDtos.isEmpty()) return@async
            val accountDto = accountDtos.first()
            _currentBand.members[AccountMapper.fromDtoToItem(accountDto)] = false
            setBandInvitation(
                BandInvitationDto(
                    AndroidUtils.generateRandomLong(),
                    currentBand.id,
                    accountDto.id,
                    false
                )
            )
        }
    }.await()

    override suspend fun acceptBandInvitation() = coroutineScope {
        async {
            val bandInvitationDtos = readBandInvitationDtos {
                it.accountId == _currentAccount.id
            }

            if (bandInvitationDtos.size != 1)
                throw RuntimeException("there can't be more invitations with the same account Id")

            val bandInvitationDto = bandInvitationDtos.first()
            bandInvitationDto.accepted = true
            setBandInvitation(bandInvitationDto)

            _currentAccount.bandId = bandInvitationDto.bandId
            updateAccount(currentAccount)
            readBand()
        }
    }.await()

    override suspend fun rejectBandInvitation() = coroutineScope {
        async {
            _firestore
                .collection(Constants.Firebase.Database.BAND_INVITATIONS)
                .document(generateDocumentNameId(Constants.Firebase.Database.BAND_INVITATIONS,
                    _currentBandInvitation.id))
                .delete()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Band Invitations ERROR $it")
                }
                .await()
            return@async
        }
    }.await()

    override fun clearData() {
        _currentAccount = Account.EMPTY
        _currentBand = Band.EMPTY
        _currentBandInvitation = BandInvitation.EMPTY
        concerts.clear()
        homeNavigationElementsMap.clear()
    }

    private suspend fun set(item: Any) {
        when(item) {
            is Account -> setItem(Constants.Firebase.Database.ACCOUNTS, AccountMapper.fromItemToDto(item))
            is Band -> {
                setItem(Constants.Firebase.Database.BANDS, BandMapper.fromItemToDbEntry(item))
                _currentBand = item
            }
            is Concert -> setItem(Constants.Firebase.Database.CONCERTS, ConcertMapper.fromItemToDto(item))
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
                _currentBand.id
            ) { result ->
                ConcertDto(
                    result.get(Constants.Concert.Fields.id) as Long,
                    result.get(Constants.Concert.Fields.name) as String,
                    result.get(Constants.Concert.Fields.dateTime) as String,
                    result.get(Constants.Concert.Fields.city) as String,
                    result.get(Constants.Concert.Fields.country) as String,
                    result.get(Constants.Concert.Fields.place) as String,
                    result.get(Constants.Concert.Fields.type) as Long,
                    result.get("bandId") as Long
                )
            }
        }
    }.await()

    private suspend fun <T : BaseModel, E : BaseDto> readItem(
        table: String,
        list: MutableList<T>,
        mapper: Mapper<T, E>,
        bandId: Long,
        action: (QueryDocumentSnapshot) -> E) = coroutineScope {
        async {
            _firestore.collection(table)
                .get()
                .addOnSuccessListener {
                    for (result in it)
                        if (bandId == result.getLong("bandId"))
                            list.add(mapper.fromDtoToItem(action(result)))
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "$table ERROR $it")
                }
                .await()
            Log.i(Constants.Firebase.Database.TAG, "$table imported successfully")
        }
    }.await()

    private suspend fun readAccount() = coroutineScope {
        async {
            val accountDBEntries = readAccountDtos {
                return@readAccountDtos it.userUid == DILocator.authenticator.currentUser?.uid
            }
            if (accountDBEntries.isEmpty()) return@async
            _currentAccount = AccountMapper.fromDtoToItem(accountDBEntries.first())
        }
    }.await()

    private suspend fun readBand() = coroutineScope {
        var bandDto: BandDto
        val accounts: MutableList<Account> = mutableListOf()
        async {

            var bandDBEntries: List<BandDto> = listOf()
            if(_currentAccount.bandId != null)
                bandDBEntries = readBandDtos(_currentAccount.bandId!!)

            if(bandDBEntries.isEmpty()) return@async
            bandDto = bandDBEntries.first()

            val bandInvitationDtos = readBandInvitationDtos {
                it.bandId == bandDto.id
            }

            val accountDbEntries = readAccountDtos { a ->
                return@readAccountDtos bandInvitationDtos.filter { it.accountId == a.id }.size == 1
            }

            accountDbEntries.forEach {
                accounts.add(AccountMapper.fromDtoToItem(it))
            }

            val map: MutableMap<Account, Boolean> = mutableMapOf()

            bandInvitationDtos.forEach { b ->
                accounts.forEach { a ->
                    if(a.id == b.accountId)
                        map[a] = b.accepted ?: false
                }
            }

            _currentBand = BandMapper.fromDbEntryToItem(bandDto, map)

            Log.i(Constants.Firebase.Database.TAG, "Accounts imported successfully")
        }
    }.await()

    private suspend fun readBandInvitation() = coroutineScope {
        async {
            val bandInvitationDtos = readBandInvitationDtos {
                it.accountId == _currentAccount.id
            }
            if(bandInvitationDtos.isEmpty()) return@async
            if (bandInvitationDtos.size > 1)
                throw RuntimeException("there can't be more invitations associated with the same account Id")

            val bandInvitationDto = bandInvitationDtos.first()

            if(bandInvitationDto.accepted == true) return@async

            val bandDtos = readBandDtos(bandInvitationDto.bandId!!)
            if (bandDtos.size != 1)
                throw RuntimeException("there can't be more bands associated with the same band invitation Id")
            val band = BandMapper.fromDbEntryToItem(bandDtos.first(), mutableMapOf())

            _currentBandInvitation = BandInvitationMapper.fromDtoToItem(
                bandInvitationDto,
                band,
                _currentAccount
            )
        }
    }.await()

    private suspend fun readAccountDtos(predicate: (account: AccountDto) -> Boolean)
    : List<AccountDto> = coroutineScope {
        async {
            val accountDbEntries = _firestore.collection(Constants.Firebase.Database.ACCOUNTS)
                .get()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Accounts ERROR $it")
                }
                .await()
                .toObjects<AccountDto>()
                .filter(predicate)
            return@async accountDbEntries
        }
    }.await()

    private suspend fun readBandInvitationDtos(action: (BandInvitationDto) -> Boolean)
    : List<BandInvitationDto> = coroutineScope {
        async {
            return@async _firestore
                .collection(Constants.Firebase.Database.BAND_INVITATIONS)
                .get()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Band Invitations ERROR $it")
                }
                .await()
                .toObjects<BandInvitationDto>()
                .filter(action)
        }
    }.await()

    private suspend fun readBandDtos(bandId: Long): List<BandDto> = coroutineScope {
        async {
            val bandDtos = _firestore.collection(Constants.Firebase.Database.BANDS)
                .get()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Accounts ERROR $it")
                }
                .await()
                .toObjects<BandDto>()
                .filter { it.id == bandId }

            if(bandDtos.size > 1)
                throw RuntimeException("there should be only one band linked to this account")
            return@async bandDtos
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