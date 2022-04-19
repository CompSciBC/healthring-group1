package com.example.healthring.todaytasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.TodaysTaskFragmentBinding

class TodaysTasksFragment : Fragment(R.layout.todays_task_fragment) {

    private var binding : TodaysTaskFragmentBinding? = null
    // private val todaysTasksViewModel : TodaysTasksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = TodaysTaskFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            todaysTasksFragment = this@TodaysTasksFragment
        }
    }

    fun goToHealthMonitorFragment() {
        findNavController().navigate(R.id.action_todaysTasksFragment2_to_healthMonitorFragment2)
    }

    fun goToFitnessFragment() {
        findNavController().navigate(R.id.action_todaysTasksFragment2_to_fitnessTrackerFragment)
    }

    fun goToProfileFragment() {
        findNavController().navigate(R.id.action_todaysTasksFragment2_to_profileFragment)
    }

    fun goToAddTaskFragment() {
        findNavController().navigate(R.id.action_todaysTasksFragment2_to_addTaskFragment)
    }

    fun goToSettingsFragment() {
        findNavController().navigate(R.id.action_todaysTasksFragment2_to_settingsFragment)
    }

}