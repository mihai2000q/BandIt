package com.bandit.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.data.repository.FriendRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {
    private val _storage = DILocator.getStorage()
    private val _repository = FriendRepository(DILocator.getDatabase())
    private val _people = MutableLiveData(_repository.people)
    val people: LiveData<List<Account>> = _people
    private val _friends = MutableLiveData(_repository.friends)
    val friends: LiveData<List<Account>> = _friends
    private val _friendRequests = MutableLiveData(_repository.friendRequests)
    val friendRequests: LiveData<List<Account>> = _friendRequests
    val friendsTabOpen = MutableLiveData(false)
    val friendsFilterName = MutableLiveData("")
    val peopleFilterName = MutableLiveData("")
    val friendRequestsFilterName = MutableLiveData("")
    suspend fun sendFriendRequest(account: Account) = coroutineScope {
        launch { _repository.sendFriendRequest(account) }.join()
        this@FriendsViewModel.refresh()
    }
    suspend fun acceptFriendRequest(account: Account) = coroutineScope {
        launch { _repository.acceptFriendRequest(account) }.join()
        this@FriendsViewModel.refresh()
    }
    suspend fun rejectFriendRequest(account: Account) = coroutineScope {
        launch { _repository.rejectFriendRequest(account) }.join()
        this@FriendsViewModel.refresh()
    }
    suspend fun unfriend(account: Account) = coroutineScope {
        launch { _repository.unfriend(account) }.join()
        this@FriendsViewModel.refresh()
    }
    suspend fun getProfilePicture(userUid: String?): ByteArray = coroutineScope {
        return@coroutineScope _storage.getProfilePicture(userUid)
    }
    fun removeBandForFriend(account: Account) {
        _repository.removeBandForFriend(account)
        this@FriendsViewModel.refresh()
    }
    fun filterFriendRequests(name: String? = null) {
        _friendRequests.value = _repository.filterFriendRequests(name)
    }
    fun filterFriends(name: String? = null) {
        _friends.value = _repository.filterFriends(name)
    }
    fun filterPeople(name: String? = null) {
        _people.value = _repository.filterPeople(name)
    }
    fun filterFriendsToBandMember(name: String? = null) {
        _friends.value = _repository.filterFriends(name).filter { it.bandId == null }
    }
    fun refresh() {
        _people.value = _repository.people
        _friends.value = _repository.friends
        _friendRequests.value = _repository.friendRequests
        if (!friendsFilterName.value.isNullOrBlank())
            this.filterFriends(friendsFilterName.value)
        if (!peopleFilterName.value.isNullOrBlank())
            this.filterPeople(peopleFilterName.value)
        if (!friendRequestsFilterName.value.isNullOrBlank())
            this.filterFriendRequests(friendRequestsFilterName.value)
    }

    companion object {
        const val TAG = Constants.Social.Friends.VIEW_MODEL_TAG
    }
}