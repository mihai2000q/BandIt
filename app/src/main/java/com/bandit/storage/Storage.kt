package com.bandit.storage

import android.net.Uri

interface Storage {
    /**
     * This method is used to set a profile picture for a user
     * @param userUid the user unique ID
     * @param imageUri the given image URI
     */
    suspend fun saveProfilePicture(userUid: String?, imageUri: Uri)
    /**
     * This method returns the profile picture for a user
     * @param userUid the user unique id
     * @return a download URI of the profile picture of that user
     */
    suspend fun getProfilePicture(userUid: String?) : Uri
}