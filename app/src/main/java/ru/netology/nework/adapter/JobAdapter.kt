package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.utils.AndroidUtils.dateUtcToMonth


interface JobOnInteractionListener {
    fun onDeleteJob(job: Job) {}

}

class JobAdapter(
    private val jobOnInteractionListener: JobOnInteractionListener,

    ) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, jobOnInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position.coerceAtMost(itemCount - 1)) ?: return
        holder.bind(job, position)
    }
}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val jobOnInteractionListener: JobOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job, pos: Int) {

        binding.apply {
            name.text = job.name
            start.text = dateUtcToMonth(job.start)
            finish.text = job.finish?.let { dateUtcToMonth(it) } ?: "Present time"
            position.text = job.position
            link.text = job.link
            delete.isVisible = true
            delete.setOnClickListener {
                jobOnInteractionListener.onDeleteJob(job)
            }

        }
    }
}


class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}
