package com.bandit.storage

import android.net.Uri
import android.util.Log
import com.bandit.constant.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseStorage : Storage {
    private val _storage = Firebase.storage

    override suspend fun saveProfilePicture(userUid: String?, imageUri: Uri) =
    coroutineScope {
        async {
            val profilePicRef = _storage.reference
                .child(userUid + "/" + Constants.Firebase.Storage.PROFILE_PIC_REFERENCE)
            profilePicRef
                .putFile(imageUri)
                .addOnSuccessListener {
                    Log.i(
                        Constants.Firebase.Storage.TAG, "Profile Pic for user $userUid" +
                                " was uploaded successfully"
                    )
                }
                .addOnFailureListener {
                    Log.e(
                        Constants.Firebase.Storage.TAG, "Profile Pic for user $userUid" +
                                " had problems while uploading"
                    )
                }
                .await()
            return@async
        }
    }.await()

    override suspend fun getProfilePicture(userUid: String?): Uri =
        coroutineScope {
            async {
                val profilePicRef = _storage.reference
                    .child(userUid + "/" + Constants.Firebase.Storage.PROFILE_PIC_REFERENCE)
                try {
                    return@async profilePicRef
                        .downloadUrl
                        .addOnSuccessListener {
                            Log.i(
                                Constants.Firebase.Storage.TAG, "Profile Pic for user $userUid" +
                                        " was uploaded successfully"
                            )
                        }
                        .addOnFailureListener {
                            Log.d(
                                Constants.Firebase.Storage.TAG, "Profile Pic for user $userUid" +
                                        " had problems while looking up"
                            )
                        }
                        .await()
                }
                catch (exception: StorageException) {
                    Log.e(
                        Constants.Firebase.Storage.TAG, "There is no Profile pic for user $userUid"
                    )
                    return@async Uri.EMPTY
                }
            }
        }.await()
}