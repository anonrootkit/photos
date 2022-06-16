package com.fiore.photos.domain.usecases.profile

import com.fiore.photos.data.repositories.ProfileRepository
import javax.inject.Inject

class FetchSelfProfileFromFirestoreUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
){
    operator fun invoke(uniqueId : String, onTaskFinished : (success : Boolean, exception : Exception?) -> Unit = {_, _ ->}) {
        profileRepository.getUserProfileFromFireStore(uniqueId, onTaskFinished)
    }
}