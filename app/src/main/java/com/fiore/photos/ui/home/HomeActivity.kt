package com.fiore.photos.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fiore.photos.R
import com.fiore.photos.databinding.ActivityHomeBinding
import com.fiore.photos.ui.splash.SplashActivity
import com.fiore.photos.ui.viewmodels.AuthViewModel
import com.fiore.photos.ui.viewmodels.ProfileViewModel
import com.fiore.photos.utils.hideStatusBar
import com.fiore.photos.utils.navigateToActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()

    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        setContentView(binding.root)

        hideStatusBar()

        initViews()
        attachObservers()
    }

    override fun onStart() {
        super.onStart()

        fetchSelfProfile()
    }

    private fun fetchSelfProfile() {
        profileViewModel.fetchSelfProfile()
    }

    private fun initViews() {
        binding.signOut.setOnClickListener { authViewModel.signOutUser() }
    }

    private fun attachObservers() {
        lifecycleScope.launchWhenStarted {
            authViewModel.signOutStatus.collect {
                if (it) authViewModel.resetSignOutStatus().also {
                    navigateToActivity(SplashActivity::class.java)
                }
            }
        }
    }
}