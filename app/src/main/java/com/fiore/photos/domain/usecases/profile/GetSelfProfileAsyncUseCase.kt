package com.fiore.photos.domain.usecases.profile

import com.fiore.photos.data.repositories.ProfileRepository
import com.fiore.photos.domain.model.UserProfileBody
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSelfProfileAsyncUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
){
    operator fun invoke() : StateFlow<UserProfileBody?> {
        return profileRepository.selfProfile
    }
}