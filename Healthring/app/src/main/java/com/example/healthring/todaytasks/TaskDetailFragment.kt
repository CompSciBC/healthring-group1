package com.example.healthring.todaytasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.TaskDetailFragmentBinding
import com.example.healthring.taskdata.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TaskDetailFragment : Fragment() {

    private val taskViewModel: TodaysTasksViewModel by activityViewModels {
        TasksViewModelFactory(
            (activity?.application as TaskApplication).database
                .taskDao()
        )
    }

    private val navigationArgs: TaskDetailFragmentArgs by navArgs()

    private var _binding: TaskDetailFragmentBinding? = null
    private val binding get() = _binding!!

    lateinit var task: Task

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TaskDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.taskId
        taskViewModel.retrieveTask(id).observe(this.viewLifecycleOwner) { selectedItem ->
            task = selectedItem
            bind(task)
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Are you sure you want to delete?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                deleteTask()
            }
            .show()
    }

    private fun bind(task: Task) {
        binding.apply {
            binding.taskTitle.text = "Title: " + task.taskTitle
            binding.taskDate.text = "Date: " + task.taskDate
            binding.taskTime.text = "Time: " + task.taskTime
            binding.taskNotes.text = "Notes: " + task.taskNotes
            deleteTask.setOnClickListener { showConfirmationDialog() }
            editTask.setOnClickListener { editTask() }
            cancelAction.setOnClickListener { goBackToTasksScreen() }
        }
    }

    private fun deleteTask() {
        taskViewModel.deleteTask(task)
        findNavController().navigateUp()
    }

    private fun editTask() {
        val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToAddTaskFragment(
            task.id
        )
        this.findNavController().navigate(action)
    }

    private fun goBackToTasksScreen(){
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}