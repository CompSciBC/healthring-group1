package com.example.healthring.todaytasks

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.AddTaskFragmentBinding

class AddTaskFragment : Fragment(R.layout.add_task_fragment) {

    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TodaysTasksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddTaskFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            addTaskFragment = this@AddTaskFragment
            todaysTasksViewModel = taskViewModel
        }
    }

    fun goToTaskFragment() {
        findNavController().navigate(R.id.action_addTaskFragment_to_todaysTasksFragment2)
    }

}