package com.bandit.ui.band

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class BandViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    fun createBand() {
        viewModelScope.launch {
            with(DILocator.database) {
                val band = Band(
                    name.value!!,
                    mutableMapOf(currentAccount to true)
                )
                add(band)
                updateAccount(
                    Account(
                        currentAccount.name,
                        currentAccount.nickname,
                        currentAccount.role,
                        band.id,
                        currentAccount.id,
                        currentAccount.userUid
                    )
                )
            }
        }
    }
}