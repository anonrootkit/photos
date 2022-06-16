package com.fiore.photos.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.fiore.photos.domain.model.UserProfileBody
import com.fiore.photos.domain.usecases.profile.FetchSelfProfileFromFirestoreUseCase
import com.fiore.photos.domain.usecases.profile.GetSelfProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getSelfProfileUseCase: GetSelfProfileUseCase,
    private val fetchSelfProfileFromFirestoreUseCase: FetchSelfProfileFromFirestoreUseCase
) : ViewModel() {
    private fun getSelfProfile() : UserProfileBody? = getSelfProfileUseCase.invoke()

    fun doesProfileAlreadyExist(): Boolean {
        val profile = getSelfProfile()
        return profile != null && profile.uniqueId.isNotBlank()
    }

    fun fetchSelfProfile() {
        val uniqueId = getSelfProfile()?.uniqueId ?: return
        fetchSelfProfileFromFirestoreUseCase.invoke(uniqueId)
    }
}