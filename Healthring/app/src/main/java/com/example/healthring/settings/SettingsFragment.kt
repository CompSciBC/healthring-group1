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
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.healthmonitor.HealthMonitorViewModel
import com.example.healthring.model.DataViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private val dataVM: DataViewModel by activityViewModels()
    private val healthMonitorViewmodel: HealthMonitorViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Settings")

        val notificationPref = findPreference<SwitchPreferenceCompat>("astheics_turnoff_sensor_colors")!!
        notificationPref.setOnPreferenceClickListener {
            dataVM.disableSensorColors.value = notificationPref.isChecked
            true
        }

        val sensorTextSizeSeekBar = findPreference<SeekBarPreference>("sensor_text_size_seekbar")!!
        sensorTextSizeSeekBar.setOnPreferenceChangeListener { preference, newValue ->
            dataVM.sensorsTextSize.value = newValue.toString().toFloat()
            Log.i("SETTINGS", "newValue: ${dataVM.sensorsTextSize.value}")
            true
        }

        val titlesTextSizeSeekBar = findPreference<SeekBarPreference>("sensor_title_text_size_seekbar")!!
        titlesTextSizeSeekBar?.setOnPreferenceChangeListener { preference, newValue ->
            dataVM.sensorTitlesTextSize.value = newValue.toString().toFloat()
            Log.i("SETTINGS", "newValue: ${dataVM.sensorTitlesTextSize.value}")
            true
        }
    }

    override fun onPause() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        super.onPause()
    }

    fun resetPassword(newPassword: String, confirmCode: String) {
        Amplify.Auth.confirmResetPassword(newPassword, confirmCode,
            {
                // reset password was successful
                Log.i("", "")
            },
            {
                // reset password failed
                Log.e("", "")
            }
        )
    }
}