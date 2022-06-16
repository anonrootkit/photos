package com.fiore.photos.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fiore.photos.ui.home.HomeActivity
import com.fiore.photos.ui.onboarding.OnBoardingActivity
import com.fiore.photos.ui.viewmodels.AuthViewModel
import com.fiore.photos.ui.viewmodels.ProfileViewModel
import com.fiore.photos.utils.TimeConstants.ONE_SECOND
import com.fiore.photos.utils.doAfter
import com.fiore.photos.utils.navigateToActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep the splash screen visible for this Activity
        splashScreen.setKeepOnScreenCondition { true }

        val userExist = profileViewModel.doesProfileAlreadyExist()

        doAfter(ONE_SECOND) {
            if (userExist) navigateToHomeActivity()
            else generateTempUserIdIfDoesNotExist()
        }

        attachObservers()
    }

    private fun attachObservers() {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.tempUserIdGenerated.collect { generated -> if (generated) navigateToOnboardingActivity() }
                }
            }
        }
    }

    private fun generateTempUserIdIfDoesNotExist() {
        val tempUserId = authViewModel.getTempUserId()
        if (tempUserId.isNullOrBlank()) authViewModel.generateTempUserId()
        else authViewModel.markTempUserIdGenerated()
    }

    private fun navigateToHomeActivity() =
        navigateToActivity(destination = HomeActivity::class.java)

    private fun navigateToOnboardingActivity() =
        navigateToActivity(destination = OnBoardingActivity::class.java)
}