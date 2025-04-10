package ru.netology.nework.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Post
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.utils.loadAvatar
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


interface OnInteractionListener {
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onPlayAudio(post: Post) {}
    fun onLike(post: Post) {}
    fun onStopAudio() {}
    fun onItemClick(post: Post, position: Int) {}

}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,

    ) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position.coerceAtMost(itemCount - 1)) ?: return
        holder.bind(post, position)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post, position: Int) {

        binding.apply {
            avatar.loadAvatar(post.authorAvatar?.let { "${post.authorAvatar}" })
            author.text = post.author
            published.text =
                ZonedDateTime.parse(post.published).withZoneSameInstant(ZoneId.systemDefault())
                    .format(
                        DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")
                    )
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = post.likeOwnerIds?.size.toString()
            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            attachmentImage.isVisible = post.attachment?.type == AttachmentType.IMAGE
            post.attachment?.let { attachmentImage.loadAttachmentView(it.url) }
            videoContainer.isVisible = post.attachment?.type == AttachmentType.VIDEO
            attachmentVideo.apply {
                if (post.attachment?.type == AttachmentType.VIDEO && !post.attachment.url.isNullOrBlank()) {
                    setVideoURI(
                        Uri.parse(post.attachment?.url)
                    )
                    setOnPreparedListener {
                        seekTo(5)
                    }

                    playVideoButton.setOnClickListener {
                        onInteractionListener.onStopAudio()
                        start()
                        playVideoButton.isVisible = false
                        setOnCompletionListener {
                            resume()
                            playVideoButton.isVisible = true
                        }
                    }
                    videoContainer.setOnClickListener {
                        pause()
                        playVideoButton.isVisible = true
                    }
                }
            }

            attachmentAudioLayout.isVisible = post.attachment?.type == AttachmentType.AUDIO
            playAudioButton.isChecked = post.isPlayingAudio
            playAudioButton.setOnClickListener {
                onInteractionListener.onPlayAudio(post)
            }
            link.isVisible = post.link?.isEmpty() == false
            link.text = post.link

            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.editContent -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            itemView.setOnClickListener {
                onInteractionListener.onItemClick(post, position)
            }


        }
    }

}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
