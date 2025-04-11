package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardUserBinding
import ru.netology.nework.dto.User


interface UsersOnInteractionListener {
    fun onEdit(user: User, position: Int) {}

}

class UsersAdapter(
    private val usersOnInteractionListener: UsersOnInteractionListener,

    ) : PagingDataAdapter<User, UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, usersOnInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position.coerceAtMost(itemCount - 1)) ?: return
        holder.bind(user, position)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val usersOnInteractionListener: UsersOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, position: Int) {

        binding.apply {

        }
    }

}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
