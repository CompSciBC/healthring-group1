package com.example.healthring.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.healthmonitor.HealthMonitorViewModel
import com.example.healthring.model.DataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {

    private val dataVM: DataViewModel by activityViewModels()

    @SuppressLint("RestrictedApi")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)
        (activity as AppCompatActivity).supportActionBar?.setShowHideAnimationEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.hot_purple_drawable))
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Settings")

        (activity as AppCompatActivity).window.setBackgroundDrawableResource(R.color.white)

        Log.i("SETTINGSPREF", "shared preferences: ${preferenceManager}")
        Log.i("SETTINGSPREF", "shared preferences name: ${preferenceManager.sharedPreferencesName}")

        resetPasswordClickListener()
        signOutClickListener()

        val notificationPref = findPreference<Preference>("email_notifications")
        notificationPref?.setOnPreferenceClickListener {
            goToNotificationsScreen()
            true
        }

//        val resetToDefaultPref = findPreference<Preference>("reset_to_default_settings")
//        notificationPref?.setOnPreferenceClickListener {
//            val notificationPref1 = findPreference<Preference>("notify_heart_rate")
//            notificationPref.isEnabled = false
//            true
//        }
    }

    private fun resetPasswordClickListener() {
        val resetPasswordPref = findPreference<Preference>("reset_password")
        resetPasswordPref?.setOnPreferenceClickListener {
            goToResetPasswordFragment()
            true
        }
    }

    private fun signOutClickListener() {
        val signOutPrf = findPreference<Preference>("sign_out")
        signOutPrf?.setOnPreferenceClickListener {
            signOut()
            true
        }
    }

    override fun onPause() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        super.onPause()
    }

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Settings")
        super.onResume()
    }

    private fun goToResetPasswordFragment() {
        findNavController().navigate(R.id.action_settingsFragment_to_resetPasswordConfirmcodeFragment)
    }

    private fun signOut() {
        Amplify.Auth.signOut(
            {
                Log.i("SETTINGS", "Sign out successful")
                goToLoginFragment()
            },
            { Log.e("SETTINGS", "Sign out failed")}
        )
    }

    private fun goToLoginFragment() {
        CoroutineScope(Dispatchers.Main).launch() {
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }
    }

    private fun goToNotificationsScreen() {
        CoroutineScope(Dispatchers.Main).launch() {
            findNavController().navigate(R.id.action_settingsFragment_to_notificationsFragment)
        }
    }
}