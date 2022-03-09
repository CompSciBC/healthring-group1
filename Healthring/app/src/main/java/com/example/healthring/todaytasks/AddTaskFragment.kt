package com.example.healthring.todaytasks

import android.app.Application
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
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
//            findNavController().navigate(R.id.action_todaysTasksFragment2_to_addTaskFragment)
            val action = AddItemFragmentDirections.actionAddTaskFragmentToTodaysTasksFragment2()
            findNavController().navigate(action)
        }
    }

    private fun updateItem() {
        if (isEntryValid()) {
            taskViewModel.updateTask(
                this.navigationArgs.taskId,
                this.binding.taskTitle.text.toString(),
                this.binding.taskDate.text.toString(),
                this.binding.taskTime.text.toString(),
                this.binding.taskNotes.text.toString()
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    private fun bind(task: Task) {
        bindng.apply{
            taskTitle.setText(task.taskTitle, TextView.BufferType.SPANNABLE)
            taskDate.setText(task.taskDate, TextView.BufferType.SPANNABLE)
            taskTime.setText(task.taskTime, TextView.BufferType.SPANNABLE)
            taskNotes.setText(task.taskNotes, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateTask() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding?.apply {
//            lifecycleOwner = viewLifecycleOwner
//            addTaskFragment = this@AddTaskFragment
//            todaysTasksViewModel = taskViewModel
//        }
        val id = navigationArgs.taskId
        if (id > 0) {
            taskViewModel.retrieveTask(id).observe(this.viewLifecycleOwner) { selectedItem ->
                task = selectedItem
                bind(task)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewTask()
            }
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