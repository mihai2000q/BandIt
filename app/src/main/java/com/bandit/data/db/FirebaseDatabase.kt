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

    override suspend fun createBand(band: Band) = coroutineScope {
        async {
            // update the account's band properties
            _currentAccount.bandId = band.id
            _currentAccount.bandName = band.name
            this@FirebaseDatabase.edit(_currentAccount)
            // add the band to database
            band.members[_currentAccount] = true
            this@FirebaseDatabase.add(band)
            // add the creator to members
            this@FirebaseDatabase.add(
                BandInvitation(
                    band = _currentBand,
                    account = _currentAccount,
                    hasAccepted = true
                )
            )
            //reject all the other bands
            bandInvitations.forEach {
                this@FirebaseDatabase.rejectBandInvitation(it)
            }
            return@async
        }
    }.await()

    override suspend fun sendBandInvitation(account: Account) = coroutineScope {
        async {
            _currentBand.members[account] = false
            this@FirebaseDatabase.add(
                BandInvitation(
                    band = _currentBand,
                    account = account,
                    hasAccepted = false
                )
            )
        }
    }.await()

    override suspend fun acceptBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        async {
            launch {
                // accept the invitation and update the database
                bandInvitation.hasAccepted = true
                this@FirebaseDatabase.add(bandInvitation)
                // remove it from the program cache,
                // as the list is used to reject all the other invitations
                bandInvitations.remove(bandInvitation)
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
                                    // finally, remove all the other invitations
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

    override suspend fun rejectBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        async { reset(bandInvitation) }
    }.await()

    override suspend fun kickBandMember(account: Account) = coroutineScope {
        async {
            account.bandId = null
            account.bandName = null
            this@FirebaseDatabase.edit(account)
            val membership = readBandInvitationDtos {
                it.accountId == account.id && it.bandId == currentBand.id && it.accepted == true
            }.first()
            this@FirebaseDatabase.remove(membership)
        }
    }.await()

    override suspend fun abandonBand() = coroutineScope {
        async {
            _currentBand = Band.EMPTY
            this@FirebaseDatabase.kickBandMember(_currentAccount)
        }
    }.await()

    override suspend fun disbandBand() = coroutineScope {
        async {
            launch {
                _currentAccount.bandId = null
                _currentAccount.bandName = null
                // remove all band invitations
                lateinit var memberships: List<BandInvitationDto>
                launch {
                    memberships = readBandInvitationDtos {
                        it.bandId == _currentBand.id
                    }
                }.invokeOnCompletion {
                    launch {
                        memberships.forEach { this@FirebaseDatabase.remove(it) }
                    }
                }
                // edit all properties from band members
                lateinit var members: List<Account>
                launch {
                    members = readAccountDtos { it.bandId == _currentBand.id }
                        .map { AccountMapper.fromDtoToItem(it) }
                }.invokeOnCompletion {
                    launch {
                        members.forEach {
                            it.bandId = null
                            it.bandName = null
                            this@FirebaseDatabase.edit(it)
                        }
                    }
                }
                launch { this@FirebaseDatabase.remove(_currentBand) }
                launch {
                    this@FirebaseDatabase.removeBandItems(
                        Constants.Firebase.Database.CONCERTS, ConcertMapper, _currentBand.id
                    )
                }
                launch {
                    this@FirebaseDatabase.removeBandItems(
                        Constants.Firebase.Database.SONGS, SongMapper, _currentBand.id
                    )
                }
                launch {
                    this@FirebaseDatabase.removeBandItems(
                        Constants.Firebase.Database.ALBUMS, AlbumMapper, _currentBand.id
                    )
                }
                launch {
                    this@FirebaseDatabase.removeBandItems(
                        Constants.Firebase.Database.EVENTS, EventMapper, _currentBand.id
                    )
                }
                launch {
                    this@FirebaseDatabase.removeBandItems(
                        Constants.Firebase.Database.TASKS, TaskMapper, _currentBand.id
                    )
                }
            }.invokeOnCompletion {
                _currentBand = Band.EMPTY
            }
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
            val dto = readDtos<FriendRequestDto>(Constants.Firebase.Database.FRIEND_REQUESTS)
                .first {
                    it.accountId == _currentAccount.id && it.friendId == account.id
                }
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
            this@FirebaseDatabase.remove(
                readDtos<FriendRequestDto>(Constants.Firebase.Database.FRIEND_REQUESTS)
                    .first {
                        it.accountId == _currentAccount.id && it.friendId == account.id
                    }
            )
        }
    }.await()

    override suspend fun unfriend(account: Account) = coroutineScope {
        async {
            val allFriends = readDtos<FriendDto>(Constants.Firebase.Database.FRIENDS)
            val friend1 = allFriends.first { it.accountId == account.id && it.friendId == _currentAccount.id }
            val friend2 = allFriends.first { it.accountId == _currentAccount.id && it.friendId == account.id }
            this@FirebaseDatabase.remove(friend1)
            this@FirebaseDatabase.remove(friend2)
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
                is UserAccountDto -> setUserAccountSetup(item)
                is Account -> setItem(Constants.Firebase.Database.ACCOUNTS, AccountMapper.fromItemToDto(item))
                is Band -> setItem(Constants.Firebase.Database.BANDS, BandMapper.fromItemToDto(item))
                is BandInvitation -> setItem(Constants.Firebase.Database.BAND_INVITATIONS,
                        BandInvitationMapper.fromItemToDto(item))
                is Concert -> setItem(Constants.Firebase.Database.CONCERTS,
                    ConcertMapper.fromItemToDto(item))
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
                is Account -> deleteItem(Constants.Firebase.Database.ACCOUNTS, item)
                is Band -> deleteItem(Constants.Firebase.Database.BANDS, item)
                is BandInvitation,
                is BandInvitationDto -> deleteItem(Constants.Firebase.Database.BAND_INVITATIONS, item as Item)
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

    private suspend fun setUserAccountSetup(userAccountDto: UserAccountDto) = coroutineScope {
        async {
            _firestore.collection(Constants.Firebase.Database.USER_ACCOUNT_SETUPS)
                .document(generateDocumentNameUserUid(userAccountDto.userUid!!))
                .set(userAccountDto)
                .await()
            return@async
        }
    }.await()

    private suspend fun readAccount(userUid: String) = coroutineScope {
        async {
            _currentAccount = readAccountDtos {
                it.userUid == userUid
            }.map { AccountMapper.fromDtoToItem(it) }.first()
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
            concerts += readAndMapItemsB(Constants.Firebase.Database.CONCERTS, ConcertMapper, _currentBand.id)
        }
    }.await()

    private suspend fun readSongs() = coroutineScope {
        async {
            songs += readAndMapItemsB(Constants.Firebase.Database.SONGS, SongMapper, _currentBand.id)
        }
    }.await()

    private suspend fun readAlbums() = coroutineScope {
        async {
            albums += readAndMapItemsB(Constants.Firebase.Database.ALBUMS, AlbumMapper, _currentBand.id)
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
            events += readAndMapItemsB(Constants.Firebase.Database.EVENTS, EventMapper, _currentBand.id)
        }
    }.await()

    private suspend fun readTasks() = coroutineScope {
        async {
            tasks += readAndMapItemsB(Constants.Firebase.Database.TASKS, TaskMapper, _currentBand.id)
        }
    }.await()

    private suspend fun readNotes() = coroutineScope {
        async {
            notes += readAndMapItemsA(Constants.Firebase.Database.NOTES, NoteMapper, _currentAccount.id)
        }
    }.await()

    private suspend fun readFriends() = coroutineScope {
        async {
            val dtos = readItemsA<FriendDto>(Constants.Firebase.Database.FRIENDS, _currentAccount.id)
            friends +=
                readAccountDtos { acc -> dtos.any { dto -> dto.friendId == acc.id } }
                .map { AccountMapper.fromDtoToItem(it) }
        }
    }.await()

    private suspend fun readFriendRequests() = coroutineScope {
        async {
            val dtos = readItemsA<FriendRequestDto>(
                Constants.Firebase.Database.FRIEND_REQUESTS, _currentAccount.id)
            friendRequests +=
                readAccountDtos { acc -> dtos.any { dto -> dto.friendId == acc.id } }
                .map { AccountMapper.fromDtoToItem(it) }
        }
    }.await()

    private suspend fun readPeople() = coroutineScope {
        async {
            people += readDtos<AccountDto>(Constants.Firebase.Database.ACCOUNTS)
                .map { AccountMapper.fromDtoToItem(it) }
            people -= _currentAccount
            people -= friendRequests.toSet()
            people -= friends.toSet()
        }
    }.await()

    private suspend fun readAccountDtos(filterPredicate: (account: AccountDto) -> Boolean)
    : List<AccountDto> = coroutineScope {
        async {
            return@async readDtos<AccountDto>(Constants.Firebase.Database.ACCOUNTS)
                .filter(filterPredicate)
        }
    }.await()

    private suspend fun readBandDtos(bandId: Long): List<BandDto> = coroutineScope {
        async {
            return@async readDtos<BandDto>(Constants.Firebase.Database.BANDS)
                .filter { it.id == bandId }
        }
    }.await()

    private suspend fun readBandInvitationDtos(filterPredicate: (BandInvitationDto) -> Boolean)
            : List<BandInvitationDto> = coroutineScope {
        async {
            return@async readDtos<BandInvitationDto>(Constants.Firebase.Database.BAND_INVITATIONS)
                .filter(filterPredicate)
        }
    }.await()

    private suspend inline fun <T : Item, reified E : TemplateBandDto> readAndMapItemsB(
        table: String,
        mapperB: MapperB<T, E>,
        bandId: Long
    ): List<T> =
    coroutineScope {
        async {
            return@async readDtos<E>(table)
                .filter { it.bandId == bandId }
                .map { mapperB.fromDtoToItem(it) }
        }
    }.await()

    private suspend inline fun <T : Item, reified E : TemplateAccountDto> readAndMapItemsA(
        table: String,
        mapperA: MapperA<T, E>,
        accountId: Long
    ): List<T> =
        coroutineScope {
            async {
                return@async readItemsA<E>(table, accountId)
                    .map { mapperA.fromDtoToItem(it) }
            }
        }.await()

    private suspend inline fun <reified E : TemplateAccountDto> readItemsA(
        table: String,
        accountId: Long
    ): List<E> =
        coroutineScope {
            async {
                return@async readDtos<E>(table)
                    .filter { it.accountId == accountId }
            }
        }.await()

    private suspend inline fun<reified E : Item> readDtos(table: String): List<E> =
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
            }
        }.await()

    private suspend inline fun<T : Item, reified E : TemplateBandDto> removeBandItems(
        table: String,
        mapperB: MapperB<T, E>,
        bandId: Long
    ) = coroutineScope {
        async {
            lateinit var items: List<T>
            launch {
                items = readAndMapItemsB(table, mapperB, bandId)
            }.invokeOnCompletion {
                launch {
                    items.forEach {
                        this@FirebaseDatabase.remove(it)
                    }
                }
            }
        }
    }.await()

    private fun generateDocumentNameUserUid(userUid: String) =
        Constants.Firebase.Database.USER_ACCOUNT_SETUPS.dropLast(1) + "-" + userUid

    private fun generateDocumentNameId(table: String, id: Long) =
        "${table.lowercase().dropLast(1)}$id"

}