package com.bandit.storage

import android.net.Uri
import android.util.Log
import com.bandit.constant.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseStorage : Storage {
    private val _storage = Firebase.storage

    override suspend fun setProfilePicture(userUid: String?, imageUri: Uri) =
    coroutineScope {
        async {
            val profilePicRef = _storage.reference
                .child(userUid + "/" + Constants.Firebase.Storage.PROFILE_PIC_REFERENCE)
            profilePicRef
                .putFile(imageUri)
                .addOnFailureListener {
                    Log.i(
                        Constants.Firebase.Storage.TAG, "Profile Pic for user $userUid " +
                                "was uploaded successfully"
                    )
                }
                .addOnSuccessListener {
                    Log.w(
                        Constants.Firebase.Storage.TAG, "Profile Pic for user $userUid " +
                                "had problems while uploading"
                    )
                }
                .await()
            return@async
        }
    }.await()

    override suspend fun getProfilePicture(userUid: String?): ByteArray =
        coroutineScope {
            async {
                val profilePicRef = _storage.reference
                    .child(userUid + "/" + Constants.Firebase.Storage.PROFILE_PIC_REFERENCE)
                return@async profilePicRef
                    .getBytes(Constants.ONE_GIGABYTE)
                    .addOnFailureListener {
                        Log.i(
                            Constants.Firebase.Storage.TAG, "Profile Pic for user $userUid " +
                                    "was uploaded successfully"
                        )
                    }
                    .addOnSuccessListener {
                        Log.w(
                            Constants.Firebase.Storage.TAG, "Profile Pic for user $userUid " +
                                    "had problems while uploading"
                        )
                    }
                    .await()
            }
        }.await()
}