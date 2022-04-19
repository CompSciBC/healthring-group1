package com.example.healthring.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.healthring.R
import com.example.healthring.healthmonitor.HealthMonitorViewModel
import com.example.healthring.model.DataViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private val dataVM: DataViewModel by activityViewModels()
    private val healthMonitorViewmodel: HealthMonitorViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)
        val notificationPref = findPreference<Preference>("email_notifications")
        notificationPref?.setOnPreferenceClickListener {
            healthMonitorViewmodel.disableSensorColor.value = true
            Log.i("SETTINGS", "Disabled Health Monitor Adaptive Sensor Colors.")
            true
        }
    }
}