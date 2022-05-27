package com.example.healthring.fitnesstracker

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.amplifyframework.core.Amplify
import com.example.healthring.R
import com.example.healthring.databinding.FitnessTrackerFragmentBinding
import com.example.healthring.model.DataViewModel
import com.example.healthring.model.Sensors
import kotlinx.coroutines.*

class FitnessTrackerFragment : Fragment(R.layout.fitness_tracker_fragment) {

    private var binding : FitnessTrackerFragmentBinding? = null
    private var preferences: SharedPreferences? = null
    private val viewModel: FitnessTrackerViewModel by viewModels()
    private val dataVM: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FitnessTrackerFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            fitnessTrackerFragment = this@FitnessTrackerFragment
            dataViewModel = dataVM
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_fitnessTrackerFragment_to_healthMonitorFragment)
    }

    fun goToTaskFragment() {
        findNavController().navigate(R.id.action_fitnessTrackerFragment_to_todaysTasksFragment2)
    }

    fun goToProfileFragment() {
        findNavController().navigate(R.id.action_fitnessTrackerFragment_to_profileFragment)
    }

    fun goToSettingsFragment() {
        findNavController().navigate(R.id.action_fitnessTrackerFragment_to_settingsFragment)
    }

    fun goToStepsGraph() {
        getReportAndGoToGraph(Sensors.STEPS)
    }

    fun goToDistanceGraph() {
        getReportAndGoToGraph(Sensors.DISTANCE)
    }

    fun goToCaloriesGraph() {
        getReportAndGoToGraph(Sensors.CALORIES)
    }

    private fun getReportAndGoToGraph(sensor: Sensors) {
        GlobalScope.launch(Dispatchers.IO) {
            dataVM.asyncGrabReportData(sensor,"day")
            GlobalScope.launch(Dispatchers.Main) {
                findNavController().navigate(R.id.action_fitnessTrackerFragment_to_graphFragment)
            }
        }
    }

    fun updateStepsTextSize() {
        binding?.fitnessStepsTracker?.textSize = preferences?.all!!["sensor_text_size_seekbar"].toString().toFloat()
        binding?.fitnessStepsTrackerTitle?.textSize = preferences?.all!!["sensor_title_text_size_seekbar"].toString().toFloat()
    }

    fun updateDistanceTextSize() {
        binding?.fitnessDistanceTracker?.textSize = preferences?.all!!["sensor_text_size_seekbar"].toString().toFloat()
        binding?.fitnessDistanceTrackerTitle?.textSize = preferences?.all!!["sensor_title_text_size_seekbar"].toString().toFloat()
    }

    fun updateCaloriesTextSize() {
        binding?.fitnessCaloriesTracker?.textSize = preferences?.all!!["sensor_text_size_seekbar"].toString().toFloat()
        binding?.fitnessCaloriesTrackerTitle?.textSize = preferences?.all!!["sensor_title_text_size_seekbar"].toString().toFloat()
    }
}