package com.fiore.photos.domain.usecases.auth

import com.fiore.photos.data.repositories.AuthRepository
import javax.inject.Inject

class GenerateUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(onTaskFinished : () -> Unit) {
        authRepository.generateTempUserId(onTaskFinished)
    }
}