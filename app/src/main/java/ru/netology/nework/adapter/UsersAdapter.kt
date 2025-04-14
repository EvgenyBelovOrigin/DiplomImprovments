package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardUserBinding
import ru.netology.nework.dto.User
import ru.netology.nework.utils.loadAvatar


interface UsersOnInteractionListener {
    fun onCheckUser(user: User, position: Int) {}

}

class UsersAdapter(
    private val usersOnInteractionListener: UsersOnInteractionListener,
    private val needToCheckUsers: Boolean

) : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, usersOnInteractionListener, needToCheckUsers)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position.coerceAtMost(itemCount - 1)) ?: return
        holder.bind(user, position)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val usersOnInteractionListener: UsersOnInteractionListener,
    private val needToCheckUsers: Boolean
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, position: Int) {

        binding.apply {
            checkUser.isVisible = needToCheckUsers
            checkUser.isChecked = user.isChecked
            checkUser.setOnClickListener {
                usersOnInteractionListener.onCheckUser(user, position)
            }
            avatar.loadAvatar(user.avatar)
            author.text = user.name
            login.text = user.login

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
