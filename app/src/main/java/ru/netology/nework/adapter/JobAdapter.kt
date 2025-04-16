package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardUserBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.User


interface JobOnInteractionListener {
    fun onCheckUser(user: User, position: Int) {}
    fun onChooseUser(user: User, position: Int) {}

}

class JobAdapter(
    private val usersOnInteractionListener: UsersOnInteractionListener,

    ) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, usersOnInteractionListener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position.coerceAtMost(itemCount - 1)) ?: return
        holder.bind(job, position)
    }
}

class JobViewHolder(
    private val binding: CardUserBinding,
    private val usersOnInteractionListener: UsersOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job, position: Int) {

        binding.apply {
//            checkUser.isVisible = needToCheckUsers
//            checkUser.isChecked = user.isChecked
            checkUser.setOnClickListener {
//                usersOnInteractionListener.onCheckUser(user, position)
            }
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
