package com.example.healthring.settings

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.ResetPasswordConfirmcodeFragmentBinding
import com.example.healthring.databinding.SignupFragmentBinding

class ResetPasswordConfirmcodeFragment : Fragment(R.layout.reset_password_confirmcode_fragment) {

    private var binding : ResetPasswordConfirmcodeFragmentBinding? = null
    private var confirmationCode: String? = null
    private var password: String? = null
    private var reenteredPassword: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Settings")

        val fragmentBinding = ResetPasswordConfirmcodeFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@ResetPasswordConfirmcodeFragment
        }
        sendConfirmationCode()
    }

    private fun sendConfirmationCode() {
        Amplify.Auth.resetPassword(Amplify.Auth.currentUser.username,
            { Log.i("SETTINGS", "Confirmation code sent to user's email: ${Amplify.Auth.currentUser.username}")},
            { Log.e("SETTINGS", "Error while trying to send confirmation code to user's email: ${Amplify.Auth.currentUser.username},  ${it.message}")}
        )
    }

    fun resetPassword() {
        if(checkForErrors()) {
            return
        }
        Amplify.Auth.confirmResetPassword(password!!, confirmationCode!!,
            {
                // reset password was successful
                Log.i("SETTINGS", "Successfully reset user's password")
                Toast.makeText(context, "Password was successfully reset.", Toast.LENGTH_LONG).show()
            },
            {
                // reset password failed
                Log.e("SETTINGS", "Failed to reset user's password")
                Toast.makeText(context, "Password could not be reset.", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun checkForErrors(): Boolean {
        if(password == null) {
            Log.e("RESETPASSWORD", "No password was entered.")
            Toast.makeText(context, "Please enter a password", Toast.LENGTH_LONG).show()
            return true
        } else if(confirmationCode == null) {
            Log.e("RESETPASSWORD", "confirmation code was not entered.")
            Toast.makeText(context, "A confirmation code was not entered.", Toast.LENGTH_LONG).show()
            return true
        }
        if(!checkIfPasswordsMatch()) {
            Log.e("RESETPASSWORD", "Passwords entered did not match.")
            Toast.makeText(context, "Passwords entered did not match.", Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }

    fun checkIfPasswordsMatch(): Boolean {
        return password == reenteredPassword
    }

    fun navigateBack() {
        findNavController().popBackStack()
    }

    fun updateConfirmCode(e: Editable) {
        confirmationCode = e.toString()
    }

    fun updatePassword(e: Editable) {
        password = e.toString()
    }

    fun updateReenteredPassword(e: Editable) {
        reenteredPassword = e.toString()
    }

}