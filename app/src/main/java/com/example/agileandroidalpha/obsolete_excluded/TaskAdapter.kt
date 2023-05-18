package com.example.agileandroidalpha.obsolete_excluded

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.agileandroidalpha.R

class TaskAdapter : ListAdapter<TaskOLD, TaskAdapter.TaskViewHolder>(TasksComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.info?.name)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TaskViewHolder(view)
            }
        }

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    class TasksComparator : DiffUtil.ItemCallback<TaskOLD>() {

        override fun areItemsTheSame(itm1: TaskOLD, itm2: TaskOLD): Boolean {
            return itm1 === itm2
        }
        override fun areContentsTheSame(itm1: TaskOLD, itm2: TaskOLD): Boolean {
            return (itm1.taskId == itm2.taskId ||
                    (itm1.info?.name == itm2.info?.name &&
                    itm1.info?.title == itm2.info?.title &&
                    itm1.altID == itm2.altID))
        }
    }

}