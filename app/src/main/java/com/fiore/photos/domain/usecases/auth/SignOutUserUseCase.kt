package com.fiore.photos.domain.usecases.auth

import com.fiore.photos.data.repositories.AuthRepository
import com.fiore.photos.data.repositories.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val scope: CoroutineScope,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(onTaskCompleted : () -> Unit) {
        scope.launch {
            authRepository.signOutUser()
            authRepository.resetTempUserId()
            profileRepository.resetSelfProfile()
            onTaskCompleted()
        }
    }
}