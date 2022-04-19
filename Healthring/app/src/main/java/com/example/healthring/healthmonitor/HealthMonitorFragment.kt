package com.example.healthring.healthmonitor

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.example.healthring.R
import com.example.healthring.databinding.HealthMonitorFragmentBinding
import com.example.healthring.model.DataViewModel
import com.example.healthring.model.Sensors
import kotlinx.coroutines.*
import java.lang.Thread.sleep


class HealthMonitorFragment : Fragment(R.layout.health_monitor_fragment){

    private var binding : HealthMonitorFragmentBinding? = null
    private val dataVM: DataViewModel by activityViewModels()
    private val viewModel: HealthMonitorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = HealthMonitorFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            // pass instance of the viewmodel and HealthMonitorFragment
            healthMonitorFragment = this@HealthMonitorFragment
            dataViewModel = dataVM
            healthMonitorViewModel = viewModel
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }
        if(!dataVM.updatingSensors) {
            GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                dataVM.updateSensorValues()
            }
            dataVM.updatingSensors = true
        }
        if(!dataVM.grabbedWeeklyData) {
            GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                dataVM.asyncGrabReportData(Sensors.H_RATE,"week")
            }
            dataVM.grabbedWeeklyData = true
        }
    }

    fun goToFitnessFragment() {
        findNavController().navigate(R.id.action_healthMonitorFragment_to_fitnessTrackerFragment)
    }

    fun goToTaskFragment() {
        findNavController().navigate(R.id.action_healthMonitorFragment_to_todaysTasksFragment2)
    }

    fun goToProfileFragment() {
        findNavController().navigate(R.id.action_healthMonitorFragment_to_profileFragment)
    }

    fun goToSettingsFragment() {
        findNavController().navigate(R.id.action_healthMonitorFragment_to_settingsFragment)
    }

    fun goToHeartRateGraph() {
        getReportAndGoToGraph(Sensors.H_RATE)
    }

    fun goToBloodPressureGraph() {
        getReportAndGoToGraph(Sensors.B_PRESSURE)
    }

    fun goToBloodOxygenGraph() {
        getReportAndGoToGraph(Sensors.B_OXYGEN)
    }

    private fun getReportAndGoToGraph(sensor: Sensors) {
        GlobalScope.launch(Dispatchers.IO) {
            dataVM.asyncGrabReportData(sensor,"day")
            GlobalScope.launch(Dispatchers.Main) {
                findNavController().navigate(R.id.action_healthMonitorFragment_to_graphFragment)
            }
        }
    }

    override fun onDestroy() {
        Log.i("HEALTHFRAGMENT", "Fragment Destroyed.")
        super.onDestroy()
    }

    override fun onPause() {
        Log.i("HEALTHFRAGMENT", "Fragment paused.")
        super.onPause()
    }

    override fun onResume() {
        Log.i("HEALTHFRAGMENT", "Fragment Resumed.")
        super.onResume()
    }

    fun updateHeartRateColor() {
        if (dataVM.heart_rate.value!! < 100) {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.blue_border)
        } else if ((dataVM.heart_rate.value!! < 125)) {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.green_border)
        } else if ((dataVM.heart_rate.value!! < 150)) {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.amber_border)
        } else {
            viewModel.hRateColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.red_border)
        }
    }

    fun updateBloodPressureColor() {
        if (dataVM.blood_pressure.value!! < 120) {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.blue_border)
        } else if ((dataVM.blood_pressure.value!! < 135)) {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.green_border)
        } else if ((dataVM.blood_pressure.value!! < 150)) {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.amber_border)
        } else {
            viewModel.bPressureColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.red_border)
        }
    }

    fun updateBloodOxygenColor() {
        if (dataVM.blood_oxygen.value!! < 70) {
            viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.red_border)
        } else if ((dataVM.blood_oxygen.value!! < 80)) {
            viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.amber_border)
        } else if ((dataVM.blood_oxygen.value!! < 90)) {
                viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.green_border)
        } else {
            viewModel.bOxygenColor.value = ContextCompat.getDrawable(requireContext(), R.drawable.blue_border)
        }
    }
}