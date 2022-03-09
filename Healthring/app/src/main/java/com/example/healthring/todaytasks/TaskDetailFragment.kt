package com.example.healthring.todaytasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.example.healthring.databinding.TaskDetailFragmentBinding

class TaskDetailFragment : Fragment() {

    private val navigationArgs: TaskDetailFragmentArgs by navArgs()

    private var _binding: TaskDetailFragmentArgs? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TaskDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun deleteTask() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}