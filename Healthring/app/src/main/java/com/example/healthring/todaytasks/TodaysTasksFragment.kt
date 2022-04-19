package com.example.healthring.todaytasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthring.R
import com.example.healthring.databinding.TodaysTaskFragmentBinding

//class TodaysTasksFragment : Fragment() {
class TodaysTasksFragment : Fragment(R.layout.todays_task_fragment) {


//    private var binding : TodaysTaskFragmentBinding? = null
    // private val todaysTasksViewModel : TodaysTasksViewModel by viewModels()
//    private val taskViewModel: TodaysTasksViewModel by activityViewModels() // milaTodaysTasksFragmentBindingn added

    private val taskViewModel: TodaysTasksViewModel by activityViewModels {
        TasksViewModelFactory(
            (activity?.application as TaskApplication).database.taskDao()
        )
    }

    private var _binding: TodaysTaskFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val fragmentBinding = TodaysTaskFragmentBinding.inflate(inflater, container, false)
//        binding = fragmentBinding
//        return fragmentBinding.root
        _binding = TodaysTaskFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            todaysTasksFragment = this@TodaysTasksFragment
//            todaysTasksViewModel = taskViewModel // milan added
        }
        val adapter = TaskListAdapter {
            val action = TodaysTasksFragmentDirections.actionTodaysTasksFragment2ToTaskDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter
        taskViewModel.allTasks.observe(this.viewLifecycleOwner) { tasks ->
            tasks.let {
                adapter.submitList(it)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.floatingActionButton.setOnClickListener {
            val action = TodaysTasksFragmentDirections.actionTodaysTasksFragment2ToAddTaskFragment()
            this.findNavController().navigate(action)
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

//    fun goToAddTaskFragment() {
//        findNavController().navigate(R.id.action_todaysTasksFragment2_to_addTaskFragment)
//    }

}