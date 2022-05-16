package com.example.healthring.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.getBinding
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.LoginFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.lang.NullPointerException

class LoginFragment : Fragment() {

    private var binding: LoginFragmentBinding? = null
    private val authViewModel: AuthViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private var tokenId = ""

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
            viewModel = authViewModel

        }

        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                when (session.identityId.type) {
                    AuthSessionResult.Type.SUCCESS ->
                        signInSuccessful(session)
                    AuthSessionResult.Type.FAILURE ->
                        Log.w("AuthQuickStart", "Not signed in.", session.identityId.error)
                }
            },
            { Log.e("AuthQuickStart", "Failed to fetch session", it) }
        )


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emailFocusListener()
    }

    private fun signInSuccessful(session: AWSCognitoAuthSession) {
        val token: String? = session.userPoolTokens.value?.idToken
        if (token != null) {
            setToken(token)
        } else {
            Log.e("Token ERROR", "Signed in failed, could not get user Id Token")
        }
    }

    private fun setToken(s: String) {
        tokenId = s
    }

    fun signIn() {
        var email: String = ""
        var password: String = ""
        try {
            email = loginViewModel.email.value!!
            password = loginViewModel.password.value!!
        } catch (e: NullPointerException) {
            Log.e("Sign in ERROR", "Either email or password field was not given. ")
        }

        Amplify.Auth.signIn(email, password,
            { result ->
                // goes to Health Monitor fragment if sign in succeeded
                handleResult(result.isSignInComplete)
                if (result.isSignInComplete) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                }
            },
            {
                Log.e("AuthQuickstart", "Failed to sign in", it)
                Snackbar.make(
                    binding?.root!!,
                    "Username or password entered was not correct.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        )
    }

    fun openSesameSignIn() {
        Amplify.Auth.signIn("alex@filbert.com", "Password",
            { result ->
                // goes to Health Monitor fragment if sign in succeeded
                handleResult(result.isSignInComplete)
                if (result.isSignInComplete) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                }
            },
            {
                Log.e("AuthQuickstart", "Failed to sign in", it)
                Snackbar.make(
                    binding?.root!!,
                    "Username or password entered was not correct.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun handleResult(isSignedIn: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isSignedIn) {
                goToHealthMonitorFragment()
            }
        }
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_healthMonitorFragment)
    }

    fun goToSignupFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }

    // ------------------------------------------------------------------------------------
    // Validate Edit Text
    private fun emailFocusListener() {
        binding?.loginEmail?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding?.loginEmail?.helperText = validEmail()
            }
        }
    }

    public fun validEmail(): String? {
        val emailText = authViewModel.email.value.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

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