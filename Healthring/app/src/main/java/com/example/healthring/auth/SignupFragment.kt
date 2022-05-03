package com.example.healthring.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import android.util.Log
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.SignupFragmentBinding
import com.google.android.material.snackbar.Snackbar

class SignupFragment : Fragment() {

    private var binding : SignupFragmentBinding? = null
    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = SignupFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            signupFragment = this@SignupFragment
            viewModel = authViewModel
        }
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_signupFragment_to_healthMonitorFragment)
    }

    fun goToLoginFragment() {
        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
    }

    fun registerNewUserAndGoToConfirmationPage() {
        val succeeded = registerNewUser()

        if (succeeded) {
            goToConfirmationFragment()
        } else {
            Snackbar.make(binding?.root!!, "Email or either password entered was not correct.", Snackbar.LENGTH_LONG).show()
        }
    }

    fun registerNewUser(): Boolean {
        // TODO: Add a try-catch to handle assertion errors for when email/pwd is null
        var succeeded = false
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), authViewModel.email.value!!)
            .build()
        Amplify.Auth.signUp(authViewModel.email.value!!, authViewModel.password.value!!, options,
            // Sign up succeeded, go to sign up fragment
            {
                Log.i("AuthQuickStart", "Sign up succeeded: $it")
                goToConfirmationFragment()
            },
            // Implement that tells the user that sign up fails.
            // use a toast
            {
                Log.e ("AuthQuickStart", "Sign up failed", it)
                Snackbar.make(binding?.root!!, "Email or either password entered was not correct.", Snackbar.LENGTH_LONG).show()
            }
        )

        return succeeded
    }

    fun goToConfirmationFragment() {
        findNavController().navigate(R.id.action_signupFragment_to_confirmationFragment)
    }



//    fun confirmNewUser() {
//        Log.i("Amplify", "confirmation code: ${authViewModel.confirmCode.value.toString()}")
//        Amplify.Auth.confirmSignUp(
//            authViewModel.email.value!!, authViewModel.confirmCode.value.toString(),
//            { result ->
//                if (result.isSignUpComplete) {
//                    Log.i("AuthQuickstart", "Confirm signUp succeeded")
//                } else {
//                    Log.i("AuthQuickstart","Confirm sign up not complete")
//                }
//            },
//            { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
//        )
//    }

}