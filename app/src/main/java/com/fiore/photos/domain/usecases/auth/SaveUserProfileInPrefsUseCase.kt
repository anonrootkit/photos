package com.fiore.photos.domain.usecases.auth

import com.fiore.photos.data.repositories.AuthRepository
import com.fiore.photos.domain.model.UserAuthBody
import javax.inject.Inject

class SaveUserProfileInPrefsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(userAuthBody: UserAuthBody, onTaskFinished : (success : Boolean, exception : Exception?) -> Unit) {
        authRepository.signInUser(userAuthBody, onTaskFinished)
    }
}