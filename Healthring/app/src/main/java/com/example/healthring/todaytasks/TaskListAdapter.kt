package com.example.healthring.todaytasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.healthring.taskdata.Task
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthring.databinding.TodaysTaskFragmentBinding
import com.example.healthring.databinding.TaskListItemBinding


class TaskListAdapter(private val onItemClicked: (Task) -> Unit) :
    ListAdapter<Task, TaskListAdapter.TaskViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
//            TodaysTaskFragmentBinding.inflate(
            TaskListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

//    class TaskViewHolder(private var binding: TodaysTaskFragmentBinding) :
    class TaskViewHolder(private var binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                taskTitle.text = task.taskTitle
                taskDate.text = task.taskDate
                taskTime.text = task.taskTime
                taskNotes.text = task.taskNotes
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.taskTitle == newItem.taskTitle
            }
        }
    }
}