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

        val enablePushNotificationsSwitch = findPreference<Preference>("enable_push_notifications")
        val pushNotificationsCategory = findPreference<PreferenceCategory>("push_notifications_category")
        enablePushNotificationsSwitch?.setOnPreferenceChangeListener { preference, newValue ->
            pushNotificationsCategory?.isEnabled = enablePushNotificationsSwitch.isEnabled
            pushNotificationsCategory?.shouldDisableView = enablePushNotificationsSwitch.isEnabled
            true
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