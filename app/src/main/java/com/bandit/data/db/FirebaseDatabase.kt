package com.bandit.data.db

import android.util.Log
import com.bandit.constant.Constants
import com.bandit.data.dto.*
import com.bandit.data.mapper.*
import com.bandit.data.model.*
import com.bandit.data.template.TemplateAccountDto
import com.bandit.data.template.TemplateBandDto
import com.bandit.data.template.Item
import com.bandit.util.AndroidUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class FirebaseDatabase : Database {
    override val concerts: MutableList<Concert> = mutableListOf()
    override val songs: MutableList<Song> = mutableListOf()
    override val albums: MutableList<Album> = mutableListOf()
    override val events: MutableList<Event> = mutableListOf()
    override val tasks: MutableList<Task> = mutableListOf()
    override val notes: MutableList<Note> = mutableListOf()
    override val people: MutableList<Account> = mutableListOf()
    override val friends: MutableList<Account> = mutableListOf()
    override val friendRequests: MutableList<Account> = mutableListOf()
    override val bandInvitations: MutableList<BandInvitation> = mutableListOf()
    override val currentAccount: Account get() = _currentAccount
    override val currentBand: Band get() = _currentBand
    private var _currentAccount = Account.EMPTY
    private var _currentBand = Band.EMPTY
    private val _firestore = Firebase.firestore

    override suspend fun init(userUid: String) = coroutineScope {
        async {
            readAccount(userUid)
            readBand()
            readBandInvitations()
            readAccountItems()
            if (_currentBand.isEmpty()) return@async
            readBandItems()
        }
    }.await()

    override suspend fun add(item: Any) = coroutineScope {
        async { set(item) }
    }.await()

    override suspend fun remove(item: Any) = coroutineScope {
        async { reset(item) }
    }.await()

    override suspend fun edit(item: Any) = coroutineScope {
        async { set(item) }
    }.await()

    override suspend fun setUserAccountSetup(
        userUid: String,
        email: String,
        isAccountSetup: Boolean
    ) = coroutineScope {
        async {
            _firestore.collection(Constants.Firebase.Database.USER_ACCOUNT_SETUPS)
                .document(generateDocumentNameUserUid(userUid))
                .set(UserAccountDto(isAccountSetup, userUid, email))
                .await()
            return@async
        }
    }.await()

    override suspend fun isUserAccountSetup(userUid: String): Boolean? = coroutineScope {
        async {
            return@async _firestore.collection(Constants.Firebase.Database.USER_ACCOUNT_SETUPS)
                .document(generateDocumentNameUserUid(userUid))
                .get()
                .await()
                .toObject(UserAccountDto::class.java)
                ?.accountSetup
        }
    }.await()

    override suspend fun createBand(name: String) = coroutineScope {
        async {
            val band = Band(
                name,
                _currentAccount.id,
                mutableMapOf()
            )
            launch {
                this@FirebaseDatabase.edit(
                    Account(
                        name = _currentAccount.name,
                        nickname = _currentAccount.nickname,
                        role = _currentAccount.role,
                        email = _currentAccount.email,
                        bandId = band.id,
                        bandName = band.name,
                        id = _currentAccount.id,
                        userUid = _currentAccount.userUid,
                    )
                )
            }.invokeOnCompletion {
                launch {
                    band.members[_currentAccount] = true
                    this@FirebaseDatabase.add(band)
                }.invokeOnCompletion {
                    launch {
                        this@FirebaseDatabase.setBandInvitation(
                            BandInvitationDto(
                                AndroidUtils.generateRandomLong(),
                                band.id,
                                _currentAccount.id,
                                true
                            )
                        )
                    }.invokeOnCompletion {
                        launch {
                            bandInvitations.forEach {
                                this@FirebaseDatabase.rejectBandInvitation(it)
                            }
                        }
                    }
                }
            }
            return@async
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

    override suspend fun sendBandInvitation(account: Account) = coroutineScope {
        async {
            _currentBand.members[account] = false
            this@FirebaseDatabase.setBandInvitation(
                BandInvitationDto(
                    AndroidUtils.generateRandomLong(),
                    currentBand.id,
                    account.id,
                    false
                )
            )
        }
    }.await()

    override suspend fun acceptBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        async {
            lateinit var dto: BandInvitationDto
            launch {
                // get the Dto of this band invitation
                dto = BandInvitationMapper.fromItemToDto(bandInvitations.first { it.id == bandInvitation.id })
            }.invokeOnCompletion {
                launch {
                    // accept the invitation and update the database
                    dto.accepted = true
                    this@FirebaseDatabase.setBandInvitation(dto)
                    bandInvitations.remove(bandInvitation)
                }.invokeOnCompletion {
                    launch {
                        // update the account by adding the band
                        _currentAccount.bandId = bandInvitation.band.id
                        _currentAccount.bandName = bandInvitation.band.name
                        this@FirebaseDatabase.edit(_currentAccount)
                    }.invokeOnCompletion {
                        // then read the new band and its items
                        launch { this@FirebaseDatabase.readBand() }
                            .invokeOnCompletion {
                                launch { this@FirebaseDatabase.readBandItems() }
                                    .invokeOnCompletion {
                                        launch {
                                            // remove all the other invitations
                                            bandInvitations.forEach {
                                                this@FirebaseDatabase.rejectBandInvitation(it)
                                            }
                                        }
                                    }
                            }
                    }
                }
            }
            return@async
        }
    }.await()

    override suspend fun rejectBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        async {
            _firestore
                .collection(Constants.Firebase.Database.BAND_INVITATIONS)
                .document(generateDocumentNameId(Constants.Firebase.Database.BAND_INVITATIONS,
                    bandInvitation.id))
                .delete()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Band Invitations ERROR $it")
                }
                .await()
            return@async
        }
    }.await()

    override suspend fun sendFriendRequest(account: Account) = coroutineScope {
        async {
            this@FirebaseDatabase.add(
                FriendRequestDto(
                    id = AndroidUtils.generateRandomLong(),
                    accountId = account.id,
                    friendId = _currentAccount.id
                )
            )
        }
    }.await()

    override suspend fun acceptFriendRequest(account: Account) = coroutineScope {
        async {
            val dto = readFriendRequestDtos {
                it.accountId == _currentAccount.id && it.friendId == account.id
            }.first()
            this@FirebaseDatabase.remove(dto)
            this@FirebaseDatabase.add(
                FriendDto(
                    id = dto.id,
                    accountId = dto.accountId,
                    friendId = dto.friendId
                )
            )
            this@FirebaseDatabase.add(
                FriendDto(
                    id = dto.id + 1,
                    accountId = dto.friendId,
                    friendId = dto.accountId
                )
            )
        }
    }.await()

    override suspend fun rejectFriendRequest(account: Account) = coroutineScope {
        async {
            val dto = readFriendRequestDtos {
                it.accountId == _currentAccount.id && it.friendId == account.id
            }.first()
            this@FirebaseDatabase.remove(dto)
        }
    }.await()

    override suspend fun isEmailInUse(email: String): Boolean =
        coroutineScope {
            async {
                return@async _firestore.collection(Constants.Firebase.Database.USER_ACCOUNT_SETUPS)
                    .get()
                    .await()
                    .toObjects<UserAccountDto>()
                    .filter { it.email?.lowercase() == email.lowercase() }
                    .size == 1
            }
        }.await()

    override suspend fun isConnected(): Boolean = coroutineScope {
        return@coroutineScope withTimeoutOrNull(Constants.TIMEOUT_INTERNET_CONNECTION_TEST) {
                // write a dummy object to database
                _firestore.collection(Constants.Firebase.Database.INTERNET_CONNECTION_COLLECTION)
                    .document(Constants.Firebase.Database.INTERNET_CONNECTION_DOCUMENT)
                    .set(InternetConnectionTestDto(true))
                    .await()
                // retrieve it
                val result =
                    _firestore.collection(Constants.Firebase.Database.INTERNET_CONNECTION_COLLECTION)
                        .document(Constants.Firebase.Database.INTERNET_CONNECTION_DOCUMENT)
                        .get()
                        .await()
                        .toObject(InternetConnectionTestDto::class.java)
                        ?.test
                        ?: false
                // then delete it
                _firestore.collection(Constants.Firebase.Database.INTERNET_CONNECTION_COLLECTION)
                    .document(Constants.Firebase.Database.INTERNET_CONNECTION_DOCUMENT)
                    .delete()
                    .await()
                // return its value, that is true by default
                // otherwise, if something went wrong, it's false
                return@withTimeoutOrNull result
        } ?: false // if it times out then it will return false instead of null
    }

    override fun clearData() {
        _currentAccount = Account.EMPTY
        _currentBand = Band.EMPTY
        concerts.clear()
        songs.clear()
        albums.clear()
        events.clear()
        tasks.clear()
        notes.clear()
        people.clear()
        friends.clear()
        friendRequests.clear()
        bandInvitations.clear()
    }

    private suspend fun set(item: Any) = coroutineScope {
        async {
            when(item) {
                is Account -> {
                    setItem(Constants.Firebase.Database.ACCOUNTS, AccountMapper.fromItemToDto(item))
                    _currentAccount = item
                }
                is Band -> {
                    setItem(Constants.Firebase.Database.BANDS, BandMapper.fromItemToDto(item))
                    _currentBand = item
                }
                is Concert -> setItem(Constants.Firebase.Database.CONCERTS, ConcertMapper.fromItemToDto(item))
                is Song -> setItem(Constants.Firebase.Database.SONGS, SongMapper.fromItemToDto(item))
                is Album -> setItem(Constants.Firebase.Database.ALBUMS, AlbumMapper.fromItemToDto(item))
                is Event -> setItem(Constants.Firebase.Database.EVENTS, EventMapper.fromItemToDto(item))
                is Task -> setItem(Constants.Firebase.Database.TASKS, TaskMapper.fromItemToDto(item))
                is Note -> setItem(Constants.Firebase.Database.NOTES, NoteMapper.fromItemToDto(item))
                is FriendRequestDto -> setItem(Constants.Firebase.Database.FRIEND_REQUESTS, item)
                is FriendDto -> setItem(Constants.Firebase.Database.FRIENDS, item)
            }
        }
    }.await()

    private suspend fun reset(item: Any) = coroutineScope {
        async {
            when (item) {
                is Account -> {
                    deleteItem(Constants.Firebase.Database.ACCOUNTS, item)
                    _currentAccount = Account.EMPTY
                }
                is Band -> {
                    deleteItem(Constants.Firebase.Database.BANDS, item)
                    _currentBand = Band.EMPTY
                }
                is Concert -> deleteItem(Constants.Firebase.Database.CONCERTS, item)
                is Song -> deleteItem(Constants.Firebase.Database.SONGS, item)
                is Album -> deleteItem(Constants.Firebase.Database.ALBUMS, item)
                is Event -> deleteItem(Constants.Firebase.Database.EVENTS, item)
                is Task -> deleteItem(Constants.Firebase.Database.TASKS, item)
                is Note -> deleteItem(Constants.Firebase.Database.NOTES, item)
                is FriendRequestDto -> deleteItem(Constants.Firebase.Database.FRIEND_REQUESTS, item)
                is FriendDto -> deleteItem(Constants.Firebase.Database.FRIENDS, item)
            }
        }
    }.await()

    private suspend fun setItem(table: String, item: Item) = coroutineScope {
        async {
            _firestore.collection(table).document(generateDocumentNameId(table, item.id))
                .set(item)
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG,
                        "${item.javaClass.name} ${item.id} - set item ERROR $it")
                }
                .await()
            return@async
        }
    }.await()

    private suspend fun deleteItem(table: String, item: Item) = coroutineScope {
        async {
            _firestore.collection(table)
                .document(generateDocumentNameId(table, item.id))
                .delete()
                .addOnFailureListener {
                    Log.e(
                        Constants.Firebase.Database.TAG,
                        "${item.javaClass.name} ${item.id} - delete item ERROR $it"
                    )
                }
                .await()
            return@async
        }
    }.await()

    private suspend fun readAccount(userUid: String) = coroutineScope {
        async {
            val accountDBEntries = readAccountDtos {
                return@readAccountDtos it.userUid == userUid
            }
            if (accountDBEntries.isEmpty()) return@async
            _currentAccount = AccountMapper.fromDtoToItem(accountDBEntries.first())
        }
    }.await()

    private suspend fun readBand() = coroutineScope {
        var bandDto: BandDto
        val accounts: MutableList<Account> = mutableListOf()
        async {

            if(_currentAccount.bandId == null) return@async

            val bandDtos = readBandDtos(_currentAccount.bandId!!)

            if(bandDtos.size != 1)
                throw RuntimeException("there can't be more than one band associated with an account")
            bandDto = bandDtos.first()

            val bandInvitationDtos = readBandInvitationDtos {
                it.bandId == bandDto.id
            }

            val accountDtos = readAccountDtos { a ->
                return@readAccountDtos bandInvitationDtos.filter { it.accountId == a.id }.size == 1
            }

            accountDtos.forEach {
                accounts.add(AccountMapper.fromDtoToItem(it))
            }

            val map: MutableMap<Account, Boolean> = mutableMapOf()

            bandInvitationDtos.forEach { b ->
                accounts.forEach { a ->
                    if(a.id == b.accountId)
                        map[a] = b.accepted ?: false
                }
            }

            _currentBand = BandMapper.fromDtoToItem(bandDto, map)

            Log.i(Constants.Firebase.Database.TAG, "Band imported successfully")
        }
    }.await()

    private suspend fun readBandInvitations() = coroutineScope {
        async {
            lateinit var dtos: List<BandInvitationDto>
            launch { dtos = readBandInvitationDtos {
                it.accountId == _currentAccount.id && it.accepted == false
            } }.join()
            for (dto in dtos) {
                lateinit var band: Band
                lateinit var account: Account
                launch {
                    band = BandMapper.fromDtoToItem(readBandDtos(dto.bandId!!).first(), mutableMapOf())
                    account = AccountMapper.fromDtoToItem(readAccountDtos { it.id == dto.accountId }.first())
                }.join()
                bandInvitations += BandInvitationMapper.fromDtoToItem(dto, band, account)
            }
        }
    }.await()

    private suspend fun readBandItems() = coroutineScope {
        async {
            readConcerts()
            readSongs()
            readAlbums()
            readEvents()
            readTasks()
        }
    }.await()

    private suspend fun readAccountItems() = coroutineScope {
        async {
            readNotes()
            readFriends()
            readFriendRequests()
            readPeople()
        }
    }.await()

    private suspend fun readConcerts() = coroutineScope {
        async {
            concerts += readBandItem(Constants.Firebase.Database.CONCERTS, ConcertMapper, _currentBand.id)
        }
    }.await()

    private suspend fun readSongs() = coroutineScope {
        async {
            songs += readBandItem(Constants.Firebase.Database.SONGS, SongMapper, _currentBand.id)
        }
    }.await()

    private suspend fun readAlbums() = coroutineScope {
        async {
            albums += readBandItem(Constants.Firebase.Database.ALBUMS, AlbumMapper, _currentBand.id)
            albums.forEach { a ->
                songs.forEach { s ->
                    if(s.albumId == a.id)
                        a.songs.add(s)
                }
            }
        }
    }.await()

    private suspend fun readEvents() = coroutineScope {
        async {
            events += readBandItem(Constants.Firebase.Database.EVENTS, EventMapper, _currentBand.id)
        }
    }.await()

    private suspend fun readTasks() = coroutineScope {
        async {
            tasks += readBandItem(Constants.Firebase.Database.TASKS, TaskMapper, _currentBand.id)
        }
    }.await()

    private suspend fun readNotes() = coroutineScope {
        async {
            notes += readAccountItem(Constants.Firebase.Database.NOTES, NoteMapper, _currentAccount.id)
        }
    }.await()

    private suspend fun readFriends() = coroutineScope {
        async {
            val table = Constants.Firebase.Database.FRIENDS
            val dtos = _firestore.collection(table)
                .get()
                .addOnSuccessListener {
                    Log.i(Constants.Firebase.Database.TAG, "$table imported successfully")
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "$table ERROR $it")
                }
                .await()
                .toObjects<FriendDto>()
                .filter { it.accountId == _currentAccount.id }

            friends +=
                readAccountDtos { acc -> dtos.any { dto -> dto.friendId == acc.id } }
                .map { AccountMapper.fromDtoToItem(it) }
        }
    }.await()

    private suspend fun readFriendRequestDtos(
        predicate: (friendRequestDto: FriendRequestDto) -> Boolean
    ): List<FriendRequestDto> =
    coroutineScope {
        async {
            val table = Constants.Firebase.Database.FRIEND_REQUESTS
            return@async _firestore.collection(table)
                .get()
                .addOnSuccessListener {
                    Log.i(Constants.Firebase.Database.TAG, "$table imported successfully")
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "$table ERROR $it")
                }
                .await()
                .toObjects<FriendRequestDto>()
                .filter(predicate)
        }
    }.await()

    private suspend fun readFriendRequests() = coroutineScope {
        async {
            val dtos = readFriendRequestDtos { it.accountId == _currentAccount.id }
            friendRequests +=
                readAccountDtos { acc -> dtos.any { dto -> dto.friendId == acc.id } }
                .map { AccountMapper.fromDtoToItem(it) }
        }
    }.await()

    private suspend fun readPeople() = coroutineScope {
        async {
            val table = Constants.Firebase.Database.ACCOUNTS
            people += _firestore.collection(table)
                .get()
                .addOnSuccessListener {
                    Log.i(Constants.Firebase.Database.TAG, "$table imported successfully")
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "$table ERROR $it")
                }
                .await()
                .toObjects<AccountDto>()
                .map { AccountMapper.fromDtoToItem(it) }
            people -= _currentAccount
            people -= friendRequests.toSet()
            people -= friends.toSet()
        }
    }.await()

    private suspend fun readAccountDtos(filterPredicate: (account: AccountDto) -> Boolean)
    : List<AccountDto> = coroutineScope {
        async {
            return@async _firestore.collection(Constants.Firebase.Database.ACCOUNTS)
                .get()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Accounts ERROR $it")
                }
                .await()
                .toObjects<AccountDto>()
                .filter(filterPredicate)
        }
    }.await()

    private suspend fun readBandDtos(bandId: Long): List<BandDto> = coroutineScope {
        async {
            return@async _firestore.collection(Constants.Firebase.Database.BANDS)
                .get()
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "Bands ERROR $it")
                }
                .await()
                .toObjects<BandDto>()
                .filter { it.id == bandId }
        }
    }.await()

    private suspend fun readBandInvitationDtos(filterPredicate: (BandInvitationDto) -> Boolean)
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
                .filter(filterPredicate)
        }
    }.await()

    private suspend inline fun <T : Item, reified E : TemplateBandDto> readBandItem(
        table: String,
        mapperB: MapperB<T, E>,
        bandId: Long
    ): List<T> =
    coroutineScope {
        async {
            return@async _firestore.collection(table)
                .get()
                .addOnSuccessListener {
                    Log.i(Constants.Firebase.Database.TAG, "$table imported successfully")
                }
                .addOnFailureListener {
                    Log.e(Constants.Firebase.Database.TAG, "$table ERROR $it")
                }
                .await()
                .toObjects<E>()
                .filter { it.bandId == bandId }
                .map { mapperB.fromDtoToItem(it) }
        }
    }.await()

    private suspend inline fun <T : Item, reified E : TemplateAccountDto> readAccountItem(
        table: String,
        mapperA: MapperA<T, E>,
        accountId: Long
    ): List<T> =
        coroutineScope {
            async {
                return@async _firestore.collection(table)
                    .get()
                    .addOnSuccessListener {
                        Log.i(Constants.Firebase.Database.TAG, "$table imported successfully")
                    }
                    .addOnFailureListener {
                        Log.e(Constants.Firebase.Database.TAG, "$table ERROR $it")
                    }
                    .await()
                    .toObjects<E>()
                    .filter { it.accountId == accountId }
                    .map { mapperA.fromDtoToItem(it) }
            }
        }.await()

    private fun generateDocumentNameUserUid(userUid: String) =
        Constants.Firebase.Database.USER_ACCOUNT_SETUPS.dropLast(1) + "-" + userUid

    private fun generateDocumentNameId(table: String, id: Long) =
        "${table.lowercase().dropLast(1)}$id"

}