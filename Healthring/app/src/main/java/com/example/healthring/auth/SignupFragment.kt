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
import kotlinx.coroutines.*

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
        GlobalScope.launch(Dispatchers.Main) {
            findNavController().navigate(R.id.action_signupFragment_to_confirmationFragment)
        }
    }

    // password matching validation
//    if (!user_passwordInput.getText().toString().equals(user_confirm_passwordInput.getText().toString()){
//
//        Toast.makeText(this, "Confirm password is not correct", Toast.LENGTH_SHORT).show();
//
//    }
}