package com.fiore.photos.domain.usecases.auth

import com.fiore.photos.data.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ResetTempUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val scope: CoroutineScope
) {
    operator fun invoke() {
        scope.launch { authRepository.resetTempUserId() }
    }
}