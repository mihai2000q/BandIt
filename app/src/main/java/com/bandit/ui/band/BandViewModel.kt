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
import com.bandit.util.FilterUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class BandViewModel : ViewModel() {
    private val _repository = BandRepository(DILocator.getDatabase())
    private val _band = MutableLiveData(_repository.band)
    val band: LiveData<Band> = _band
    private val _members = MutableLiveData(band.value?.members ?: mutableMapOf())
    val members: LiveData<MutableMap<Account, Boolean>> = _members
    private val _bandInvitations = MutableLiveData(_repository.bandInvitations)
    val bandInvitations: LiveData<List<BandInvitation>> = _bandInvitations
    val name = MutableLiveData<String>()
    val bandTabOpen = MutableLiveData(false)
    val filterBandInvitationsName = MutableLiveData("")
    val filterMembersName = MutableLiveData("")
    suspend fun createBand(creatorId: Long) = coroutineScope {
        launch {
            _repository.createBand(
                Band(
                    name = name.value!!,
                    creator = creatorId,
                    members = mutableMapOf()
                )
            )
        }.join()
        this@BandViewModel.refresh()
    }

    suspend fun sendBandInvitation(account: Account) = coroutineScope {
        launch { _repository.sendBandInvitation(account) }.join()
        this@BandViewModel.refresh()
    }

    suspend fun acceptBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        launch { _repository.acceptBandInvitation(bandInvitation) }.join()
        this@BandViewModel.refresh()
    }

    suspend fun rejectBandInvitation(bandInvitation: BandInvitation) = coroutineScope {
        launch { _repository.rejectBandInvitation(bandInvitation) }.join()
        this@BandViewModel.refresh()
    }
    
    suspend fun kickBandMember(account: Account) = coroutineScope {
        launch { _repository.kickBandMember(account) }.join()
        this@BandViewModel.refresh()
    }

    suspend fun abandonBand() = coroutineScope {
        launch { _repository.abandonBand() }.join()
        this@BandViewModel.refresh()
    }

    suspend fun disbandBand() = coroutineScope {
        launch { _repository.disbandBand() }.join()
        this@BandViewModel.refresh()
    }

    fun filterBandInvitations(bandName: String?) {
        _bandInvitations.value = _repository.filterBandInvitations(bandName)
    }

    fun filterBandMembers(name: String?) {
        _members.value = _band.value?.members?.filter { FilterUtils.filter(it.key.name, name) }?.toMutableMap()
    }

    fun refresh() {
        _band.value = _repository.band
        _members.value = _band.value?.members
        _bandInvitations.value = _repository.bandInvitations
        if(!filterMembersName.value.isNullOrBlank())
            this.filterBandMembers(filterMembersName.value)
        if(!filterBandInvitationsName.value.isNullOrBlank())
            this.filterBandInvitations(filterBandInvitationsName.value)
    }

    companion object {
        const val TAG = Constants.Social.Band.VIEW_MODEL_TAG
    }
}