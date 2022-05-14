package com.example.healthring.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.ConfirmationFragmentBinding
import com.example.healthring.databinding.SignupFragmentBinding

class ConfirmationFragment : Fragment() {

    private var binding : ConfirmationFragmentBinding? = null
    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = ConfirmationFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            confirmationFragment = this@ConfirmationFragment
            viewModel = authViewModel
        }
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_signupFragment_to_healthMonitorFragment)
    }

    // For the back to sign up button
    fun navigateUp() {
        findNavController().navigateUp()
    }



    fun confirmNewUser() {
        Amplify.Auth.confirmSignUp(
            authViewModel.email.value!!, authViewModel.confirmCode.value.toString(),
            { result ->
                if (result.isSignUpComplete) {
                    Log.i("AuthQuickstart", "Confirm signUp succeeded")
                } else {
                    Log.i("AuthQuickstart","Confirm sign up not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
        )
    }

    fun resendConfirmationCode() {
        Amplify.Auth.resendSignUpCode(
            authViewModel.email.value!!,
            { Log.i("AuthQuickstart", "Resend confirmation code succeeded") },
            { Log.e("AuthQuickstart", "Failed to resend confirm sign up", it) }
        )
    }
}