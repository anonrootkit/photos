package com.fiore.photos.ui.viewmodels

import android.util.Pair
import androidx.lifecycle.ViewModel
import com.fiore.photos.domain.model.UserAuthBody
import com.fiore.photos.domain.model.UserProfileBody
import com.fiore.photos.domain.usecases.auth.*
import com.fiore.photos.domain.usecases.profile.UpdateSelfProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val generateUserIdUseCase: GenerateUserIdUseCase,
    private val getTempUserIdUseCase: GetTempUserIdUseCase,
    private val signUpUserUseCase: SignUpUserUseCase,
    private val signInUserUseCase: SignInUserUseCase,
    private val resetTempUserIdUseCase: ResetTempUserIdUseCase,
    private val updateSelfProfileUseCase: UpdateSelfProfileUseCase,
    private val signOutUserUseCase: SignOutUserUseCase
) : ViewModel() {

    private val _tempUserIdGenerated = MutableStateFlow(false)
    val tempUserIdGenerated = _tempUserIdGenerated.asStateFlow()

    private val _signUpStatus = MutableStateFlow<Pair<Boolean, Exception?>?>(null)
    val signUpStatus = _signUpStatus.asStateFlow()

    private val _signInStatus = MutableStateFlow<Pair<Boolean, Exception?>?>(null)
    val signInStatus = _signInStatus.asStateFlow()

    private val _signOutStatus = MutableStateFlow<Boolean>(false)
    val signOutStatus = _signOutStatus.asStateFlow()

    fun getTempUserId() : String? {
        return getTempUserIdUseCase.invoke()
    }

    fun generateTempUserId() {
        generateUserIdUseCase.invoke { markTempUserIdGenerated() }
    }

    fun markTempUserIdGenerated() {
        _tempUserIdGenerated.update { true }
    }

    fun resetTempUserIdGenerated() {
        _tempUserIdGenerated.update { false }
    }

    fun resetSignUpStatus() {
        _signUpStatus.update { null }
    }

    fun resetSignInStatus() {
        _signInStatus.update { null }
    }

    fun resetSignOutStatus() {
        _signOutStatus.update { false }
    }

    fun signUpUser(userAuthBody: UserAuthBody) {
        signUpUserUseCase.invoke(userAuthBody) { isSuccessful, exception ->
            if (isSuccessful){
                resetTempUserIdUseCase.invoke()
                updateSelfProfileUseCase.invoke(UserProfileBody.getBasicProfile(uniqueId = userAuthBody.uniqueId!!))
            }
            _signUpStatus.update { isSuccessful to exception }
        }
    }

    fun signInUser(userAuthBody: UserAuthBody) {
        signInUserUseCase.invoke(userAuthBody) { isSuccessful, exception ->
            if (isSuccessful) {
                resetTempUserIdUseCase.invoke()
                updateSelfProfileUseCase.invoke(UserProfileBody.getBasicProfile(uniqueId = userAuthBody.uniqueId!!))
            }

            _signInStatus.update { isSuccessful to exception }
        }
    }

    fun signOutUser() {
        signOutUserUseCase.invoke {
            _signOutStatus.update { true }
        }
    }
}