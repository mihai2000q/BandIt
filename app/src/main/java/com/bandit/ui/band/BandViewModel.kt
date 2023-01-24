package com.bandit.ui.band

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.data.db.entry.BandInvitationDBEntry
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import kotlinx.coroutines.launch

class BandViewModel : ViewModel() {
    val band = MutableLiveData<Band>()
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    init {
        band.value = DILocator.database.currentBand
    }
    fun createBand() {
        viewModelScope.launch {
            with(DILocator.database) {
                val band = Band(
                    name.value!!,
                    currentAccount.id,
                    mutableMapOf(currentAccount to true)
                )
                this@BandViewModel.band.value = band
                add(band)
                setBandInvitationDBEntry(
                    BandInvitationDBEntry(
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
            }
        }
    }

    fun sendBandInvitation() {
        viewModelScope.launch {
                DILocator.database.sendBandInvitation(email.value ?: "")
        }
    }
}