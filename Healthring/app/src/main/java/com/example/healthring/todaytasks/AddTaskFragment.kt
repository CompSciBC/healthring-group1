package com.example.healthring.todaytasks

import android.app.Application
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.healthring.R
import com.example.healthring.databinding.AddTaskFragmentBinding
import com.example.healthring.taskdata.Task

class AddTaskFragment : Fragment() {

    private val taskViewModel: TodaysTasksViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as TaskApplication).database
                .taskDao()
        )
    }

    lateinit var task: Task

    private val navigationArgs: TaskDetailFragmentArgs by navArgs()

    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddTaskFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun isEntryValid(): Boolean {
        return taskViewModel.isEntryValid(
            binding.taskTitle.text.toString(),
            binding.taskDate.text.toString(),
            binding.taskTime.text.toString(),
            binding.taskNotes.text.toString()
        )
    }

    private fun addNewTask() {
        if (isEntryValid()) {
            taskViewModel.addNewTask(
                binding.taskTitle.text.toString(),
                binding.taskDate.text.toString(),
                binding.taskTime.text.toString(),
                binding.taskNotes.text.toString(),
            )
            findNavController().navigate(R.id.action_todaysTasksFragment2_to_addTaskFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            addTaskFragment = this@AddTaskFragment
            todaysTasksViewModel = taskViewModel
        }
            binding.saveAction.setOnClickListener {
                addNewTask()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

}