package com.fiore.photos.domain.usecases.profile

import com.fiore.photos.data.repositories.ProfileRepository
import com.fiore.photos.domain.model.UserProfileBody
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSelfProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
){
    operator fun invoke() : UserProfileBody? {
        return profileRepository.getSelfProfileNow()
    }
}