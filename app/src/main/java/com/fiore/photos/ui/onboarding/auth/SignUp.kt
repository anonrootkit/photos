package com.fiore.photos.ui.onboarding.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fiore.photos.R
import com.fiore.photos.databinding.FragmentSignUpBinding
import com.fiore.photos.domain.model.UserAuthBody
import com.fiore.photos.ui.home.HomeActivity
import com.fiore.photos.ui.viewmodels.AuthViewModel
import com.fiore.photos.utils.navigateToActivity
import com.fiore.photos.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUp : Fragment(R.layout.fragment_sign_up) {
    private lateinit var binding : FragmentSignUpBinding

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)
        binding.lifecycleOwner = this


        initViews()
        attachObservers()
    }

    private fun initViews() {
        checkIfTempUserIdExist()

        binding.uniqueIdWrapper.apply {
            isHelperTextEnabled = true
            helperText = getString(R.string.use_this_unique_id_desc)
        }

        binding.signUpButton.setOnClickListener { validateCredsAndSignUp() }

        binding.navigateToSignIn.setOnClickListener { findNavController().navigateUp() }
    }

    private fun attachObservers() {
        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.tempUserIdGenerated.collect { generated ->
                        if (generated) checkIfTempUserIdExist().also { authViewModel.resetTempUserIdGenerated() }
                    }
                }

                launch {
                    authViewModel.signUpStatus.collect {
                        it?.let {
                            val (isSuccessful, exception) = it

                            if (isSuccessful) {
                                navigateToActivity(destination = HomeActivity::class.java).also {
                                    showToast(getString(R.string.sign_up_successful))
                                }
                            }else{
                                binding.signUpButton.isEnabled = true
                                exception?.message?.showToast(context = requireContext())
                            }

                            authViewModel.resetSignUpStatus()
                        }
                    }
                }
            }
        }
    }

    private fun validateCredsAndSignUp() {
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
            authViewModel.signUpUser(userAuthBody = UserAuthBody(uniqueId = uniqueId, code = code))
                .also { binding.signUpButton.isEnabled = false }
    }

    private fun checkIfTempUserIdExist() {
        val tempUserIdExist =  authViewModel.getTempUserId()
        if (tempUserIdExist.isNullOrBlank()) authViewModel.generateTempUserId()
        else binding.uniqueId.setText(tempUserIdExist)
    }

}