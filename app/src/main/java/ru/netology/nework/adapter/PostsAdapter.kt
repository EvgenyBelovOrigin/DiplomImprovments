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
import ru.netology.nework.utils.AndroidUtils.dateUtcToStringDateTime
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.utils.loadAvatar


interface OnInteractionListener {
    fun onEdit(post: Post, position: Int) {}
    fun onRemove(post: Post, position: Int) {}
    fun onPlayAudio(post: Post, position: Int) {}
    fun onLike(post: Post, position: Int) {}
    fun onStopAudio() {}
    fun onItemClick(post: Post, position: Int) {}
    fun onVideoPlay(position: Int) {}

}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,

    ) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int,
        payloads: List<Any?>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                (it as? PayLoadPost)?.let { payload ->
                    holder.bindPayload(payload)
                }

            }
        }
    }

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
            published.text = dateUtcToStringDateTime(post.published)
            content.text = post.content.replace("\n", "")
            like.isChecked = post.likedByMe
            like.text = post.likeOwnerIds?.size.toString()
            like.setOnClickListener {
                onInteractionListener.onLike(post, position)
            }
            attachmentImage.isVisible = post.attachment?.type == AttachmentType.IMAGE
            post.attachment?.let { attachmentImage.loadAttachmentView(it.url) }
            videoContainer.isVisible = post.attachment?.type == AttachmentType.VIDEO
            attachmentVideo.apply {

                if (post.attachment?.type == AttachmentType.VIDEO && post.attachment.url.isNotBlank()) {
                    setVideoURI(
                        Uri.parse(post.attachment.url)
                    )
                    setOnPreparedListener {
                        seekTo(5)
                    }

                    playVideoButton.setOnClickListener {
                        onInteractionListener.onVideoPlay(position)
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
                onInteractionListener.onPlayAudio(post, position)
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
                                onInteractionListener.onRemove(post, position)
                                true
                            }

                            R.id.editContent -> {
                                onInteractionListener.onEdit(post, position)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            cardPostConstraint.setOnClickListener {
                onInteractionListener.onItemClick(post, position)
            }


        }
    }

    fun bindPayload(payload: PayLoadPost) {
        payload.likedByMe?.let {
            binding.like.isChecked = it
        }
        payload.content?.let {
            binding.content.text = it
        }
        payload.likeOwnerIds?.let {
            binding.like.text = it.size.toString()
        }
    }

}

data class PayLoadPost(
    val likedByMe: Boolean? = null,
    val content: String? = null,
    val likeOwnerIds: List<Int>?,

    )

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any? =

        PayLoadPost(
            likedByMe = newItem.likedByMe.takeIf { it != oldItem.likedByMe },
            content = newItem.content.takeIf { it != oldItem.content },
            likeOwnerIds = newItem.likeOwnerIds.takeIf { it != oldItem.likeOwnerIds }
        )
}
