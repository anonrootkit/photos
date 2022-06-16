package com.fiore.photos.domain.usecases.profile

import com.fiore.photos.data.repositories.ProfileRepository
import com.fiore.photos.domain.model.UserProfileBody
import javax.inject.Inject

class UpdateSelfProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
){
    operator fun invoke(userProfileBody: UserProfileBody) {
        profileRepository.updateSelfProfile(userProfileBody)
    }
}