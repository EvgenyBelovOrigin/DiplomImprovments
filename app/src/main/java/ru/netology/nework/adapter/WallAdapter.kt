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
import ru.netology.nework.utils.AndroidUtils.dateUtcToString
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.utils.loadAvatar


interface WallOnInteractionListener {
    fun onEdit(post: Post, position: Int) {}
    fun onRemove(post: Post, position: Int) {}
    fun onPlayAudio(post: Post, position: Int) {}
    fun onLike(post: Post, position: Int) {}
    fun onStopAudio() {}
    fun onItemClick(post: Post, position: Int) {}
    fun onVideoPlay(position: Int) {}

}

class WallAdapter(
    private val wallOnInteractionListener: WallOnInteractionListener,

    ) : PagingDataAdapter<Post, WallViewHolder>(WallDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WallViewHolder(binding, wallOnInteractionListener)
    }

    override fun onBindViewHolder(holder: WallViewHolder, position: Int) {
        val post = getItem(position.coerceAtMost(itemCount - 1)) ?: return
        holder.bind(post, position)
    }
}

class WallViewHolder(
    private val binding: CardPostBinding,
    private val wallOnInteractionListener: WallOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post, position: Int) {

        binding.apply {
            avatar.loadAvatar(post.authorAvatar?.let { "${post.authorAvatar}" })
            author.text = post.author
            published.text = dateUtcToString(post.published)
            content.text = post.content.replace("\n", "")
            like.isChecked = post.likedByMe
            like.text = post.likeOwnerIds?.size.toString()
            like.setOnClickListener {
                wallOnInteractionListener.onLike(post, position)
            }
            attachmentImage.isVisible = post.attachment?.type == AttachmentType.IMAGE
            post.attachment?.let { attachmentImage.loadAttachmentView(it.url) }
            videoContainer.isVisible = post.attachment?.type == AttachmentType.VIDEO
            attachmentVideo.apply {

                if (post.attachment?.type == AttachmentType.VIDEO && !post.attachment.url.isNullOrBlank()) {
                    setVideoURI(
                        Uri.parse(post.attachment.url)
                    )
                    setOnPreparedListener {
                        seekTo(5)
                    }

                    playVideoButton.setOnClickListener {
                        wallOnInteractionListener.onVideoPlay(position)
                        wallOnInteractionListener.onStopAudio()
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
                wallOnInteractionListener.onPlayAudio(post, position)
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
                                wallOnInteractionListener.onRemove(post, position)
                                true
                            }

                            R.id.editContent -> {
                                wallOnInteractionListener.onEdit(post, position)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            cardPostConstraint.setOnClickListener {
                wallOnInteractionListener.onItemClick(post, position)
            }


        }
    }

}

class WallDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
