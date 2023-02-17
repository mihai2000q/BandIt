package com.bandit.storage

import android.net.Uri

interface Storage {
    suspend fun setProfilePicture(userUid: String?, imageUri: Uri)
    suspend fun getProfilePicture(userUid: String?) : ByteArray
}