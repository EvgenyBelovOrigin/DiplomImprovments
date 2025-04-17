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
import ru.netology.nework.databinding.CardEventBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Event
import ru.netology.nework.utils.AndroidUtils.dateUtcToStringDateTime
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.utils.loadAvatar


interface EventsOnInteractionListener {
    fun onEdit(event: Event, position: Int) {}
    fun onRemove(event: Event, position: Int) {}
    fun onPlayAudio(event: Event, position: Int) {}
    fun onLike(event: Event, position: Int) {}
    fun onStopAudio() {}
    fun onItemClick(event: Event, position: Int) {}
    fun onVideoPlay(position: Int) {}
}

class EventsAdapter(
    private val eventsOnInteractionListener: EventsOnInteractionListener,

    ) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, eventsOnInteractionListener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position.coerceAtMost(itemCount - 1)) ?: return
        holder.bind(event, position)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val eventOnInteractionListener: EventsOnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event, position: Int) {

        binding.apply {
            avatar.loadAvatar(event.authorAvatar?.let { "${event.authorAvatar}" })
            author.text = event.author
            published.text = dateUtcToStringDateTime(event.published)
            content.text = event.content.replace("\n", "")
            like.isChecked = event.likedByMe
            like.text = event.likeOwnerIds?.size.toString()
            like.setOnClickListener {
                eventOnInteractionListener.onLike(event, position)
            }
            attachmentImage.isVisible = event.attachment?.type == AttachmentType.IMAGE
            event.attachment?.let { attachmentImage.loadAttachmentView(it.url) }
            videoContainer.isVisible = event.attachment?.type == AttachmentType.VIDEO
            attachmentVideo.apply {

                if (event.attachment?.type == AttachmentType.VIDEO && !event.attachment.url.isNullOrBlank()) {
                    setVideoURI(
                        Uri.parse(event.attachment.url)
                    )
                    setOnPreparedListener {
                        seekTo(5)
                    }

                    playVideoButton.setOnClickListener {
                        eventOnInteractionListener.onVideoPlay(position)
                        eventOnInteractionListener.onStopAudio()
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

            playAudioButton.isVisible = event.attachment?.type == AttachmentType.AUDIO
            playAudioButton.isChecked = event.isPlayingAudio
            playAudioButton.setOnClickListener {
                eventOnInteractionListener.onPlayAudio(event, position)
            }
            link.isVisible = event.link?.isEmpty() == false
            link.text = event.link
            typeOfEvent.text = event.type.toString()
            dateOfEvent.text = dateUtcToStringDateTime(event.datetime)

            menu.isVisible = event.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                eventOnInteractionListener.onRemove(event, position)
                                true
                            }

                            R.id.editContent -> {
                                eventOnInteractionListener.onEdit(event, position)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            cardPostConstraint.setOnClickListener {
                eventOnInteractionListener.onItemClick(event, position)
            }


        }
    }

}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}
