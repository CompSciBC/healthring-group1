package com.example.healthring.todaytasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthring.R
import com.example.healthring.databinding.TodaysTaskFragmentBinding

class TodaysTasksFragment : Fragment(R.layout.todays_task_fragment) {

    private var binding : TodaysTaskFragmentBinding? = null
    // private val todaysTasksViewModel : TodaysTasksViewModel by viewModels()
    private val taskViewModel: TodaysTasksViewModel by activityViewModels() // milan added

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
            todaysTasksViewModel = taskViewModel // milan added
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

}

//class ItemListFragment : Fragment() {
//
//    private val viewModel: InventoryViewModel by activityViewModels {
//        InventoryViewModelFactory(
//            (activity?.application as InventoryApplication).database.itemDao()
//        )
//    }
//
//    private var _binding: ItemListFragmentBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = ItemListFragmentBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val adapter = ItemListAdapter {
//            val action = ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(it.id)
//            this.findNavController().navigate(action)
//        }
//        binding.recyclerView.adapter = adapter
//        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
//            items.let {
//                adapter.submitList(it)
//            }
//        }
//        binding.recyclerView.adapter = adapter
//        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
//        binding.floatingActionButton.setOnClickListener {
//            val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(
//                getString(R.string.add_fragment_title)
//            )
//            this.findNavController().navigate(action)
//        }
//    }
//}