package com.example.healthring.todaytasks

import android.app.Application
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class AddTaskFragment : Fragment() {

    private val taskViewModel: TodaysTasksViewModel by activityViewModels {
        TasksViewModelFactory(
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
        binding.timeButton.setOnClickListener {
            openTimePicker()
        }
        binding.dateButton.setOnClickListener {
            openDatePicker()
        }
        return binding.root
    }

    private fun openDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.show(childFragmentManager, "tag")

//        datePicker.addOnPositiveButtonClickListener {
//            datePicker.
//        }
    }

    private fun openTimePicker() {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Set Alarm")
                .build()
        picker.show(childFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            val pickedHour: Int = picker.hour
            val pickedMinute: Int = picker.minute
            Log.d("TimeTest", "$pickedHour:$pickedMinute")
            val formatedTime: String = when {
                pickedHour > 12 -> {
                    if(pickedMinute < 10) {
                        "${pickedHour - 12}:0${pickedMinute} PM"
                    } else {
                        "${pickedHour - 12}:${pickedMinute} PM"
                    }
                }
                pickedHour == 12 -> {
                    if(pickedMinute < 10) {
                        "${pickedHour}:0${pickedMinute} PM"
                    } else {
                        "${pickedHour}:${pickedMinute} PM"
                    }
                }
                pickedHour == 0 -> {
                    if(pickedMinute < 10) {
                        "${pickedHour + 12}:0${pickedMinute} AM"
                    } else {
                        "${pickedHour + 12}:${pickedMinute} AM"
                    }
                }
                else -> {
                    if(pickedMinute < 10) {
                        "${pickedHour}:0${pickedMinute} AM"
                    } else {
                        "${pickedHour}:${pickedMinute} AM"
                    }
                }
            }
            binding.taskTime.setText(formatedTime)
        }
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
            val action = AddTaskFragmentDirections.actionAddTaskFragmentToTodaysTasksFragment2()
            findNavController().navigate(action)
        }
    }

    private fun updateTask() {
        if (isEntryValid()) {
            taskViewModel.updateTask(
                this.navigationArgs.taskId,
                this.binding.taskTitle.text.toString(),
                this.binding.taskDate.text.toString(),
                this.binding.taskTime.text.toString(),
                this.binding.taskNotes.text.toString()
            )
            val action = AddTaskFragmentDirections.actionAddTaskFragmentToTodaysTasksFragment2()
            findNavController().navigate(action)
        }
    }

    private fun bind(task: Task) {
        binding.apply {
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
            taskViewModel.retrieveTask(id).observe(this.viewLifecycleOwner) { selectedTask ->
                task = selectedTask
                bind(task)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewTask()
            }
        }
    }

//    fun goToTaskFragment() {
//        findNavController().navigate(R.id.action_addTaskFragment_to_todaysTasksFragment2)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}