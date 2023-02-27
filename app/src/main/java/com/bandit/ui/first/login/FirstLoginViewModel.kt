package com.bandit.ui.first.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants

class FirstLoginViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val role = MutableLiveData<BandItEnums.Account.Role>()

    companion object {
        const val TAG = Constants.FirstLogin.VIEW_MODEL_TAG
    }
}