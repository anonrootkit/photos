package com.fiore.photos.data.repositories

import androidx.datastore.core.DataStore
import com.fiore.photos.UserProfile
import com.fiore.photos.domain.model.UserProfileBody
import com.fiore.photos.utils.FireStoreConstants.USER_FOLLOWERS_COUNT_COLUMN
import com.fiore.photos.utils.FireStoreConstants.USER_FOLLOWING_COUNT_COLUMN
import com.fiore.photos.utils.FireStoreConstants.USER_HAS_PROFILE_PUBLIC_COLUMN
import com.fiore.photos.utils.FireStoreConstants.USER_NAME_COLUMN
import com.fiore.photos.utils.FireStoreConstants.USER_PROFILE_COLLECTION
import com.fiore.photos.utils.FireStoreConstants.USER_UNIQUE_ID_COLUMN
import com.fiore.photos.utils.toUserProfileBody
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val userProfileDataStore: DataStore<UserProfile>,
    private val firestore: FirebaseFirestore,
    private val scope : CoroutineScope
) {
    val selfProfile = userProfileDataStore.data.map {
        it.toUserProfileBody()
    }.stateIn(scope, SharingStarted.Lazily, null)

    fun getSelfProfileNow() : UserProfileBody? {
        return runBlocking {
            try {
                userProfileDataStore.data.map { it.toUserProfileBody() }.first()
            }catch (exception : NoSuchElementException){
                null
            }
        }
    }

    fun updateSelfProfile(userProfileBody: UserProfileBody) {
        scope.launch {
            userProfileDataStore.updateData { preferences ->
                preferences.toBuilder()
                    .setUniqueId(userProfileBody.uniqueId)
                    .setName(userProfileBody.name)
                    .setFollowersCount(userProfileBody.followersCount)
                    .setFollowingCount(userProfileBody.followingCount)
                    .setIsPublicAccount(userProfileBody.isPublicAccount)
                    .build()
            }
        }
    }

    fun saveUserProfileOnFireStore(userProfileBody: UserProfileBody, onTaskFinished : (success : Boolean, exception : Exception?) -> Unit) {
        val profileBlock = firestore.collection(USER_PROFILE_COLLECTION).document(userProfileBody.uniqueId)
        profileBlock.set(userProfileBody).addOnCompleteListener {
            if (it.isSuccessful) {
                updateSelfProfile(userProfileBody)
                onTaskFinished(true, null)
            }else{
                onTaskFinished(false, it.exception)
            }
        }
    }

    fun getUserProfileFromFireStore(userUniqueId : String, onTaskFinished : (success : Boolean, exception : Exception?) -> Unit) {
        val profileBlock = firestore.collection(USER_PROFILE_COLLECTION).document(userUniqueId)
        profileBlock.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = it.result[USER_UNIQUE_ID_COLUMN] as? String

                if (userId.isNullOrBlank()) {
                    val basicProfile = UserProfileBody.getBasicProfile(uniqueId = userUniqueId)
                    saveUserProfileOnFireStore(userProfileBody = basicProfile) { success, exception ->
                        if (success) onTaskFinished(true, null)
                        else onTaskFinished(false, exception)
                    }
                } else{
                    UserProfileBody(
                        uniqueId = it.result[USER_UNIQUE_ID_COLUMN] as String,
                        name = it.result[USER_NAME_COLUMN] as String,
                        followersCount = it.result[USER_FOLLOWERS_COUNT_COLUMN] as String,
                        followingCount = it.result[USER_FOLLOWING_COUNT_COLUMN] as String,
                        isPublicAccount = it.result[USER_HAS_PROFILE_PUBLIC_COLUMN] as Boolean,
                    ).also { updateSelfProfile(userProfileBody = it) }

                    onTaskFinished(true, null)
                }
            } else {
                onTaskFinished(false, it.exception)
            }
        }
    }

    suspend fun resetSelfProfile() {
        userProfileDataStore.updateData { preferences ->
            preferences.toBuilder().clear().build()
        }
    }

}