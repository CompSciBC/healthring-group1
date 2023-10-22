package com.example.healthring.fitnesstracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.FitnessTrackerFragmentBinding

class FitnessTrackerFragment : Fragment(R.layout.fitness_tracker_fragment) {

    private var binding : FitnessTrackerFragmentBinding? = null

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

}