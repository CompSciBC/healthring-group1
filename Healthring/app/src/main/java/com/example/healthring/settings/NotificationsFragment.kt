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
import androidx.preference.*
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.healthmonitor.HealthMonitorViewModel
import com.example.healthring.model.DataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationsFragment : PreferenceFragmentCompat() {

    private val dataVM: DataViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notifcations_screen, rootKey)

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setTitle("Settings")

        emailHeartRateClickListener()
        emailBloodPressureClickListener()
        emailBloodOxygenClickListener()
        emailEmergencyContactTextListener()
        emailDoctorContactTextListener()

        val enableEmailNotificationsSwitch = findPreference<SwitchPreferenceCompat>("enable_email_notifications")
        enableEmailNotificationsSwitch?.setOnPreferenceClickListener {
            dataVM.enableEmailNotifications = enableEmailNotificationsSwitch.isChecked
            Log.d("Email Notifications", "${dataVM.enableEmailNotifications}")
            true
        }

//        val enablePushNotificationsSwitch = findPreference<Preference>("enable_push_notifications")
//        val pushNotificationsCategory = findPreference<PreferenceCategory>("push_notifications_category")
//        enablePushNotificationsSwitch?.setOnPreferenceChangeListener { preference, newValue ->
//            pushNotificationsCategory?.isEnabled = enablePushNotificationsSwitch.isEnabled
//            pushNotificationsCategory?.shouldDisableView = enablePushNotificationsSwitch.isEnabled
//            true
//        }
//
//        val enableEmailNotificationsSwitch = findPreference<Preference>("enable_email_notifications")
//        val emailNotificationsCategory = findPreference<PreferenceCategory>("email_notifications_category")
//        enableEmailNotificationsSwitch?.setOnPreferenceChangeListener { preference, newValue ->
//            emailNotificationsCategory?.isEnabled = enableEmailNotificationsSwitch.isEnabled
//            emailNotificationsCategory?.shouldDisableView = enableEmailNotificationsSwitch.isEnabled
//            true
//        }

    }

    private fun emailHeartRateClickListener() {
        val emailHeartRateSwitch = findPreference<SwitchPreferenceCompat>("email_heart_rate")
        emailHeartRateSwitch?.setOnPreferenceClickListener {
            dataVM.emailHeartRate = emailHeartRateSwitch.isChecked
            Log.d("HeartRate Email", "${dataVM.emailHeartRate}")
            true
        }
    }

    private fun emailBloodPressureClickListener() {
        val emailBloodPressureSwitch = findPreference<SwitchPreferenceCompat>("email_blood_pressure")
        emailBloodPressureSwitch?.setOnPreferenceClickListener {
            dataVM.emailBloodPressure = emailBloodPressureSwitch.isChecked
            Log.d("BloodPressure Email", "${dataVM.emailBloodPressure}")
            true
        }
    }

    private fun emailBloodOxygenClickListener() {
        val emailBloodOxygenSwitch = findPreference<SwitchPreferenceCompat>("email_blood_oxygen")
        emailBloodOxygenSwitch?.setOnPreferenceClickListener {
            dataVM.emailBloodOxygen = emailBloodOxygenSwitch.isChecked
            Log.d("BloodOxygen Email", "${dataVM.emailBloodOxygen}")
            true
        }
    }

    private fun emailEmergencyContactTextListener() {
        val emailEmergencyContact = findPreference<EditTextPreference>("email_contact")
        emailEmergencyContact?.setOnBindEditTextListener {
            dataVM.emergencyEmail = emailEmergencyContact.text.toString()
        }
    }

    private fun emailDoctorContactTextListener() {
        val emailDoctorContact = findPreference<EditTextPreference>("email_doctor")
        emailDoctorContact?.setOnBindEditTextListener {
            dataVM.doctorEmail = emailDoctorContact.text.toString()
        }
    }

    override fun onPause() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        super.onPause()
    }

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar?.setTitle("Settings")
        super.onResume()
    }
}