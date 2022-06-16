package com.fiore.photos.domain.usecases.auth

import com.fiore.photos.data.repositories.AuthRepository
import javax.inject.Inject

class GetTempUserIdUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() : String? {
        return authRepository.getTempUserId()
    }
}