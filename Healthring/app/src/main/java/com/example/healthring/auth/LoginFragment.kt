package com.example.healthring.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.Tokens
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.LoginFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.wait
import java.lang.NullPointerException

class LoginFragment : Fragment(R.layout.fitness_tracker_fragment) {

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

    private fun signInSuccessful(session: AWSCognitoAuthSession) {
        val token: String? = session.userPoolTokens.value?.idToken
        if (token != null) {
            setToken(token)
//            goToHealthMonitorFragment()
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
                Snackbar.make(binding?.root!!, "Username or password entered was not correct.", Snackbar.LENGTH_LONG).show()
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

    fun signOut() {
        Amplify.Auth.signOut(
            { Log.i("AuthQuickstart", "Signed out successfully") },
            { Log.e("AuthQuickstart", "Sign out failed", it) }
        )
    }

    private fun callDatabase() {
        val endpoint = "https://vu102pm7vg.execute-api.us-west-2.amazonaws.com/prod/sensors"
        val client = OkHttpClient()

        val url: HttpUrl = endpoint.toHttpUrl().newBuilder()
            .addQueryParameter("email", "alex@filbert.com")
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", tokenId)
            .build()
        Log.i("TOKEN ID:", tokenId)
        Log.i("REQUEST", request.toString())

        try {
            val response = client.newCall(request).execute()
            val message = response.body?.string()
            Log.i("DYNAMODB REQUEST", "SUCCESS Message: $message")
            Log.i("DYNAMODB REQUEST", "SUCCESS Receive Response At Millis: ${response.receivedResponseAtMillis - response.sentRequestAtMillis}")
            Log.i("DYNAMODB REQUEST", "SUCCESS: " + response.code)
        } catch (e: Exception) {
            Log.i("DYNAMODB REQUEST", "Something went horribly wrong...$e")
        }
    }

    fun runCallDatabase() {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    callDatabase()
                } catch (e: InterruptedException) {
                }
            }
        }
        thread.start()
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_healthMonitorFragment)
    }

    fun goToSignupFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }
}