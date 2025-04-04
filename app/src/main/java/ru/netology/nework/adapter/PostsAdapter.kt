package ru.netology.nework.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.coroutineScope
import ru.netology.nework.BuildConfig
import ru.netology.nework.dao.PostDao
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Post
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.utils.loadAvatar
import kotlin.math.min


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onShowAttachmentViewFullScreen(post: Post) {}
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
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    private val baseUrl = BuildConfig.BASE_URL

    fun bind(post: Post) {
        binding.apply {
            avatar.loadAvatar(post.authorAvatar?.let { "${post.authorAvatar}" })
            author.text = post.id.toString()
            published.text = post.attachment?.url
            content.text = post.attachment?.type.toString()
            like.isChecked = post.likedByMe
            like.text = post.likeOwnerIds?.size.toString()
            attachmentImage.isVisible = post.attachment?.type == AttachmentType.IMAGE
            post.attachment?.let { attachmentImage.loadAttachmentView(it.url)}
            attachmentVideo.isVisible = post.attachment?.type == AttachmentType.VIDEO
            attachmentVideo.apply {
                if (post.attachment?.type == AttachmentType.VIDEO && !post.attachment.url.isNullOrBlank()) {
                    val mediaController = MediaController(context)
                    setMediaController(mediaController)
                    mediaController.show()


                    setVideoURI(
                        Uri.parse(post.attachment?.url)
                    )
                    setOnPreparedListener {
                        seekTo(1)
                        pause()
                    }
                    setOnCompletionListener {
                        stopPlayback()
                    }
                }
            }// todo add play button on video
            attachmentAudio.isVisible = post.attachment?.type == AttachmentType.AUDIO
            attachmentAudio.apply {
                if (post.attachment?.type == AttachmentType.AUDIO && !post.attachment.url.isNullOrBlank()) {
                    val mediaController = MediaController(context)
                    setMediaController(mediaController)
                    mediaController.show()
                    setVideoURI(
                        Uri.parse(post.attachment?.url)
                    )
                    setOnPreparedListener {
                        pause()
                    }
                    setOnCompletionListener {
                        stopPlayback()
                    }
                }
            }//todo view like player


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
