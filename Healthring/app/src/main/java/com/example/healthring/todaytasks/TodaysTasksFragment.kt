package com.example.healthring.todaytasks

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.healthring.R

class TodaysTasksFragment : Fragment() {

    private lateinit var viewModel: TodaysTasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(TodaysTasksViewModel::class.java)

        return inflater.inflate(R.layout.old_todays_tasks_fragment, container, false)
    }
}