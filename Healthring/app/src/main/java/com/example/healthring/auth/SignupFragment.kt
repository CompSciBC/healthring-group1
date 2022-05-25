package com.example.healthring.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import android.util.Log
import android.util.Patterns
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.SignupFragmentBinding
import com.google.android.material.snackbar.Snackbar

class SignupFragment : Fragment() {

    private var binding: SignupFragmentBinding? = null
    private val authViewModel: AuthViewModel by viewModels()

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

    fun registerNewUser() {
        // TODO: Add a try-catch to handle assertion errors for when email/pwd is null

        if (authViewModel.email.value == null) {
            Snackbar.make(binding?.root!!, "Please enter a email.", Snackbar.LENGTH_LONG).show()
            return

        }

        if (authViewModel.password.value == null || authViewModel.password.value == "") {
            Snackbar.make(binding?.root!!, "Please enter a password.", Snackbar.LENGTH_LONG).show()
            return
        }

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
                Log.e("AuthQuickStart", "Sign up failed", it)
                Snackbar.make(
                    binding?.root!!,
                    "Email or either password entered was not correct.",
                    Snackbar.LENGTH_LONG
                ).show()

            }
        )

    }

    fun goToConfirmationFragment() {
        findNavController().navigate(R.id.action_signupFragment_to_confirmationFragment)
    }

//    private fun emailFocusListener() {
//
//        binding?.loginEmail?.setOnFocusChangeListener { _, hasFocus ->
//            Log.i("EmailTest", "TestTest")
//            if (!hasFocus) {
//                binding?.loginEmail?.helperText = validEmail()
//            }
//        }
//    }

//    public fun validEmail(): String? {
//        val emailText = authViewModel.email.value.toString()
//        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
//            return "Invalid Email Address"
//        }
//        return null
//    }
//
//    private fun passwordFocusListener() {
//
//        binding.passwordEditText.setOnFocusChangeListener { _, focused ->
//            if (!focused) {
//                bindingTest.passwordContainer.helperText = validPassword()
//            }
//        }
//
//    }
//
//    private fun validPassword(): String? {
//        val passwordText = bindingTest.passwordEditText.text.toString()
//        if (passwordText.length < 8) {
//            return "Minimum 8 Character Password"
//        }
//        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
//            return "Must Contain 1 Upper-case Character"
//        }
//        if (!passwordText.matches(".*[a-z].*".toRegex())) {
//            return "Must Contain 1 Lower-case Character"
//        }
//        if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
//            return "Must Contain 1 Special Character (@#\$%^&+=)"
//        }
//        return null
//    }
}