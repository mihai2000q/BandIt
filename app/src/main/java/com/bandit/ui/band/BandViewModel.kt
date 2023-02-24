package com.bandit.ui.band

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.data.model.BandInvitation
import com.bandit.data.repository.BandRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class BandViewModel : ViewModel() {
    private val _database = DILocator.getDatabase()
    private val _band = MutableLiveData(_database.currentBand)
    val band: LiveData<Band> = _band
    private val _members = MutableLiveData(band.value?.members ?: mutableMapOf())
    val members: LiveData<MutableMap<Account, Boolean>> = _members
    private val _repository = BandRepository(_database)
    private val _bandInvitations = MutableLiveData(_repository.bandInvitations)
    val bandInvitations: LiveData<List<BandInvitation>> = _bandInvitations
    val name = MutableLiveData<String>()
    val bandTabOpen = MutableLiveData(false)
    suspend fun createBand() = viewModelScope.launch {
        launch { _repository.createBand(name.value!!) }.join()
        refresh()
    }

    suspend fun sendBandInvitation(account: Account) = viewModelScope.launch {
        launch { _repository.sendBandInvitation(account) }.join()
        refresh()
    }

    suspend fun acceptBandInvitation(bandInvitation: BandInvitation) = viewModelScope.launch {
        launch { _repository.acceptBandInvitation(bandInvitation) }.join()
        refresh()
    }

    suspend fun rejectBandInvitation(bandInvitation: BandInvitation) = viewModelScope.launch {
        launch { _repository.rejectBandInvitation(bandInvitation) }.join()
        refresh()
    }

    private fun refresh() {
        _band.value = _database.currentBand
        _members.value = _band.value?.members
        _bandInvitations.value = _repository.bandInvitations
    }

    companion object {
        const val TAG = Constants.Social.Band.VIEW_MODEL_TAG
    }
}