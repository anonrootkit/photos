package com.fiore.photos.ui.onboarding.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fiore.photos.R
import com.fiore.photos.databinding.FragmentSignInBinding
import com.fiore.photos.domain.model.UserAuthBody
import com.fiore.photos.ui.home.HomeActivity
import com.fiore.photos.ui.viewmodels.AuthViewModel
import com.fiore.photos.utils.navigateToActivity
import com.fiore.photos.utils.safeNavigate
import com.fiore.photos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignIn : Fragment(R.layout.fragment_sign_in) {
    private lateinit var binding : FragmentSignInBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)
        binding.lifecycleOwner = this


        initViews()
        attachObservers()

    }

    private fun attachObservers() {
        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.signInStatus.collect {
                        it?.let {
                            val (isSuccessful, exception) = it

                            if (isSuccessful) {
                                navigateToActivity(destination = HomeActivity::class.java).also {
                                    showToast(getString(R.string.sign_up_successful))
                                }
                            }else{
                                binding.signInButton.isEnabled = true
                                exception?.message?.showToast(context = requireContext())
                            }

                            authViewModel.resetSignInStatus()
                        }
                    }
                }
            }
        }
    }

    private fun initViews() {
        binding.signInButton.setOnClickListener { validateCredsAndSignIn() }

        binding.navigateToSignUp.setOnClickListener{
            safeNavigate(SignInDirections.actionSignInToSignUp())
        }
    }

    private fun validateCredsAndSignIn() {
        val uniqueId = binding.uniqueId.text?.trim()?.toString()
        val code = binding.code.text?.trim()?.toString()

        var hasValidCreds = true

        if (uniqueId.isNullOrBlank())
            binding.uniqueIdWrapper.apply {
                isErrorEnabled = true
                error = getString(R.string.invalid_unique_id)
            }.also {
                hasValidCreds = false
            }


        if (code.isNullOrBlank())
            binding.codeWrapper.apply {
                isErrorEnabled = true
                error = getString(R.string.invalid_code_entered)
            }.also {
                hasValidCreds = false
            }

        if (hasValidCreds)
            authViewModel.signInUser(userAuthBody = UserAuthBody(uniqueId = uniqueId, code = code))
                .also { binding.signInButton.isEnabled = false }
    }

}