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

class TaskDetailFragment : Fragment() {

    private val taskViewModel: TodaysTasksViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as TaskApplication).database
                .taskDao()
        )
    }

    private val navigationArgs: TaskDetailFragmentArgs by navArgs()

    private var _binding: TaskDetailFragmentArgs? = null
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

    private fun deleteTask() {
        taskViewModel.deleteTask(task)
        findNavController().navigateUp()
    }

    private fun editItem() {
        val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToAddItemFragment(
            task.id
        )
        this.findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}