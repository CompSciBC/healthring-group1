package com.example.healthring.fitnesstracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.FitnessTrackerFragmentBinding
import com.example.healthring.model.DataViewModel
import com.example.healthring.model.Sensors

class FitnessTrackerFragment : Fragment(R.layout.fitness_tracker_fragment) {

    private var binding : FitnessTrackerFragmentBinding? = null
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

    fun goToStepsGraph() {
        dataVM.getReportData(Sensors.STEPS)
        findNavController().navigate(R.id.action_fitnessTrackerFragment_to_graphFragment)
    }

    fun goToDistanceGraph() {
        dataVM.getReportData(Sensors.DISTANCE)
        findNavController().navigate(R.id.action_fitnessTrackerFragment_to_graphFragment)
    }

    fun goToCaloriesGraph() {
        dataVM.getReportData(Sensors.CALORIES)
        findNavController().navigate(R.id.action_fitnessTrackerFragment_to_graphFragment)
    }

}