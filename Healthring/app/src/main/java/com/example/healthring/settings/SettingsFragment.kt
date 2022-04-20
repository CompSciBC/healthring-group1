package com.example.healthring.settings

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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Settings")

        disableSensorColorsClickListener()
        sensorTextSizeSliderChangeListener()
        titlesTextSizeSliderChangeListener()
        resetPasswordClickListener()
        signOutClickListener()
    }

    private fun disableSensorColorsClickListener() {
        val disableSensorColorsPref = findPreference<SwitchPreferenceCompat>("astheics_turnoff_sensor_colors")
        disableSensorColorsPref?.setOnPreferenceClickListener {
            dataVM.disableSensorColors.value = disableSensorColorsPref.isChecked
            true
        }
    }

    private fun sensorTextSizeSliderChangeListener() {
        val sensorTextSizeSeekBar = findPreference<SeekBarPreference>("sensor_text_size_seekbar")
        sensorTextSizeSeekBar?.setOnPreferenceChangeListener { preference, newValue ->
            dataVM.sensorsTextSize.value = newValue.toString().toFloat()
            Log.i("SETTINGS", "newValue: ${dataVM.sensorsTextSize.value}")
            true
        }
    }

    private fun titlesTextSizeSliderChangeListener() {
        val titlesTextSizeSeekBar = findPreference<SeekBarPreference>("sensor_title_text_size_seekbar")
        titlesTextSizeSeekBar?.setOnPreferenceChangeListener { preference, newValue ->
            dataVM.sensorTitlesTextSize.value = newValue.toString().toFloat()
            Log.i("SETTINGS", "newValue: ${dataVM.sensorTitlesTextSize.value}")
            true
        }
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
}