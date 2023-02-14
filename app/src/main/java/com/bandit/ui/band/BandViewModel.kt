package com.bandit.ui.band

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.Constants
import com.bandit.data.db.dto.BandInvitationDto
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BandViewModel : ViewModel() {
    private val _database = DILocator.database
    private val _band = MutableLiveData(_database.currentBand)
    val band: LiveData<Band> = _band
    private val _members = MutableLiveData(band.value?.members ?: mutableMapOf())
    val members: LiveData<MutableMap<Account, Boolean>> = _members
    val name = MutableLiveData<String>()
    suspend fun createBand() = coroutineScope {
        with(_database) {
            val band = Band(
                name.value!!,
                currentAccount.id,
                mutableMapOf(currentAccount to true)
            )
            this.add(band)
            this.setBandInvitation(
                BandInvitationDto(
                    AndroidUtils.generateRandomLong(),
                    band.id,
                    currentAccount.id,
                    true
                )
            )
            this.updateAccount(
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
            refresh()
        }
    }

    suspend fun sendBandInvitation(email: String) = coroutineScope {
        launch { _database.sendBandInvitation(email) }.join()
        refresh()
    }

    fun refresh() {
        _band.value = _database.currentBand
        _members.value = _band.value?.members
    }

    companion object {
        const val TAG = Constants.Band.VIEW_MODEL_TAG
    }
}