package com.example.healthring.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthException
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.FitnessTrackerFragmentBinding
import com.example.healthring.databinding.HealthMonitorFragmentBinding
import com.example.healthring.databinding.LoginFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.lang.Exception

class LoginFragment : Fragment(R.layout.fitness_tracker_fragment) {

    private var binding: LoginFragmentBinding? = null
    private val mainViewModel: MainViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = LoginFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            loginFragment = this@LoginFragment
            loginVM = loginViewModel
            viewModel = mainViewModel
        }
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_healthMonitorFragment)
    }

    fun goToSignupFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }

    fun signIn() {
        val email: String = loginViewModel.email.value!!
        val password: String = loginViewModel.password.value!!

        Amplify.Auth.signIn(email, password,
            { result ->
                handleResult(result.isSignInComplete)
                if (result.isSignInComplete) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                }
            },
            {
                Log.e("AuthQuickstart", "Failed to sign in", it)
                Snackbar.make(binding?.root!!, "Could not sign in", Snackbar.LENGTH_LONG).show()
            }
        )
    }


    fun handleResult(isSignedIn: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isSignedIn) {
                goToHealthMonitorFragment()
            }
        }
    }

    fun async_signOut() = GlobalScope.async {
        Amplify.Auth.signOut(
            { Log.i("AuthQuickstart", "Signed out successfully") },
            { Log.e("AuthQuickstart", "Sign out failed", it) }
        )
    }

    fun async_fetch_session() = GlobalScope.async {
        Amplify.Auth.fetchAuthSession(
            { Log.i("AmplifyQuickstart", "Auth session = $it") },
            { Log.e("AmplifyQuickstart", "Failed to fetch auth session") }
        )
    }
}