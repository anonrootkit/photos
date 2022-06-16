package com.fiore.photos.data.repositories

import com.fiore.photos.data.sources.preferences.AuthPreferences
import com.fiore.photos.domain.model.UserAuthBody
import com.fiore.photos.utils.FireStoreConstants.USER_AUTH_CODE
import com.fiore.photos.utils.FireStoreConstants.USER_AUTH_COLLECTION
import com.fiore.photos.utils.FireStoreConstants.USER_UNIQUE_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authPrefs : AuthPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val coroutineScope: CoroutineScope
) {
    private suspend fun setTempUserId(userId : String) = authPrefs.setTempUserId(userId)

    fun getTempUserId(): String? = authPrefs.getTempUserId()

    suspend fun resetTempUserId() = authPrefs.resetTempUserId()

    fun generateTempUserId(onTaskFinished : () -> Unit) {
        firebaseAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful)
                firebaseAuth.currentUser?.uid?.let { id ->
                    coroutineScope.launch { setTempUserId(userId = id)   }
                }

            onTaskFinished()
        }
    }

    private fun getUserAuthBodyIfAlreadyExists(
        userAuthBody: UserAuthBody,
        onTaskFinished: (success: Boolean, exception: Exception?, authBody: UserAuthBody?) -> Unit
    ) {
        val userBlock = firestore.collection(USER_AUTH_COLLECTION).document(userAuthBody.uniqueId!!)

        userBlock.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val authBody = UserAuthBody(
                    uniqueId = it.result[USER_UNIQUE_ID] as? String,
                    code = it.result[USER_AUTH_CODE] as? String
                )

                onTaskFinished(
                    it.isSuccessful,
                    null,
                    authBody
                )
            } else onTaskFinished(false, it.exception, null)

        }
    }

    fun signUpUser(userAuthBody: UserAuthBody, onTaskFinished : (success : Boolean, exception : Exception?) -> Unit) {
        getUserAuthBodyIfAlreadyExists(userAuthBody) { isSuccess, exception, authBody ->
            if (isSuccess){
                if (authBody?.code.isNullOrBlank()) {
                    val userBlock = firestore.collection(USER_AUTH_COLLECTION).document(userAuthBody.uniqueId!!)
                    userBlock.set(userAuthBody).addOnCompleteListener { onTaskFinished(it.isSuccessful, it.exception)  }
                } else
                    onTaskFinished(false, IllegalStateException("User already exists, sign in instead"))
            }else
                onTaskFinished(false, exception)
        }
    }

    fun signInUser(userAuthBody: UserAuthBody, onTaskFinished : (success : Boolean, exception : Exception?) -> Unit) {
        getUserAuthBodyIfAlreadyExists(userAuthBody) { isSuccess, exception, authBody ->
            if (isSuccess){
                val hasValidCreds = authBody?.uniqueId == userAuthBody.uniqueId && authBody?.code == userAuthBody.code
                if (hasValidCreds) onTaskFinished(true, null)
                else onTaskFinished(false, IllegalStateException("Wrong id or code entered"))
            }
            else
                onTaskFinished(false, exception)
        }
    }

    fun signOutUser() {
        firebaseAuth.signOut()
    }
}