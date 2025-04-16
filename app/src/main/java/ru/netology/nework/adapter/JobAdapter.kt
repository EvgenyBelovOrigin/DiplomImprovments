package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.dto.Job


interface JobOnInteractionListener {

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

    fun bind(job: Job, position: Int) {

        binding.apply {
//            checkUser.isVisible = needToCheckUsers
//            checkUser.isChecked = user.isChecked
//            checkUser.setOnClickListener {
//                usersOnInteractionListener.onCheckUser(user, position)
//        }
//            avatar.loadAvatar(user.avatar)
//            author.text = user.name
//            login.text = user.login
//            if (!needToCheckUsers)
//                itemView.setOnClickListener {
//                    usersOnInteractionListener.onChooseUser(user, position)
//                }

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
