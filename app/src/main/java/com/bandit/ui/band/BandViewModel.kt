package com.bandit.ui.band

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.data.db.dto.BandInvitationDto
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import kotlinx.coroutines.launch

class BandViewModel : ViewModel() {
    private val _band = MutableLiveData(DILocator.database.currentBand)
    val band: LiveData<Band> = _band
    private val _members = MutableLiveData(band.value?.members ?: mutableMapOf())
    val members: LiveData<MutableMap<Account, Boolean>> = _members
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    fun createBand() {
        viewModelScope.launch {
            with(DILocator.database) {
                val band = Band(
                    name.value!!,
                    currentAccount.id,
                    mutableMapOf(currentAccount to true)
                )
                _band.value = band
                add(band)
                setBandInvitationDBEntry(
                    BandInvitationDto(
                        AndroidUtils.generateRandomLong(),
                        band.id,
                        currentAccount.id,
                        true
                    )
                )
                updateAccount(
                    Account(
                        currentAccount.name,
                        currentAccount.nickname,
                        currentAccount.role,
                        band.id,
                        currentAccount.email,
                        currentAccount.id,
                        currentAccount.userUid
                    )
                )
                _members.value?.set(currentAccount, true)
            }
        }
    }

    fun sendBandInvitation() {
        viewModelScope.launch {
            DILocator.database.sendBandInvitation(email.value ?: "")
        }
        _band.value = DILocator.database.currentBand
        _members.value = _band.value?.members
    }
}