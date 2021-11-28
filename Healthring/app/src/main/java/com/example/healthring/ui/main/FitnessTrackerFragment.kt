package com.example.healthring.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.FitnessTrackerFragmentBinding
import com.example.healthring.databinding.HealthMonitorFragmentBinding

class FitnessTrackerFragment : Fragment(R.layout.fitness_tracker_fragment) {

    private var binding : FitnessTrackerFragmentBinding? = null
    private val mainViewModel : MainViewModel by viewModels()

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
            viewModel = mainViewModel
        }
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_fitnessTrackerFragment_to_healthMonitorFragment)
    }

}