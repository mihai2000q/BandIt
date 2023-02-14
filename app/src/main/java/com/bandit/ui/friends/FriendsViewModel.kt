package com.bandit.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.data.repository.FriendRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {
    private val _repository = FriendRepository(DILocator.database)
    private val _people = MutableLiveData(_repository.people)
    val people: LiveData<List<Account>> = _people
    private val _friends = MutableLiveData(_repository.friends)
    val friends: LiveData<List<Account>> = _friends
    private val _friendRequests = MutableLiveData(_repository.friendRequests)
    val friendRequests: LiveData<List<Account>> = _friendRequests
    fun sendFriendRequest(account: Account) = viewModelScope.launch {
        _repository.sendFriendRequest(account)
        refresh()
    }
    fun acceptFriendRequest(account: Account) = viewModelScope.launch {
        _repository.acceptFriendRequest(account)
        refresh()
    }
    fun rejectFriendRequest(account: Account) = viewModelScope.launch {
        _repository.rejectFriendRequest(account)
        refresh()
    }
    private fun refresh() {
        _people.value = _repository.people
        _friends.value = _repository.friends
        _friendRequests.value = _repository.friendRequests
    }
    companion object {
        const val TAG = Constants.Social.Friends.VIEW_MODEL_TAG
    }
}