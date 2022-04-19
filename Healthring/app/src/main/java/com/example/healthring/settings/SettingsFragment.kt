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
import androidx.preference.SwitchPreferenceCompat
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

        val notificationPref = findPreference<SwitchPreferenceCompat>("astheics_turnoff_sensor_colors")
        notificationPref?.setOnPreferenceClickListener {
            dataVM.disableSensorColors.value = notificationPref.isChecked
            true
        }
    }

    override fun onPause() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        super.onPause()
    }
}