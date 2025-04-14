package ru.netology.nework.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.CardEventBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.utils.AndroidUtils.dateUtcToString
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.utils.loadAvatar
import ru.netology.nework.viewmodel.EventViewModel
import ru.netology.nework.viewmodel.UserViewModel

@AndroidEntryPoint
class DetailEventFragment : Fragment() {


    private val viewModel: EventViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = CardEventBinding.inflate(
            inflater,
            container,
            false
        )


        viewModel.edited.observe(viewLifecycleOwner) { event ->
            binding.apply {
                avatar.loadAvatar(event.authorAvatar?.let { "${event.authorAvatar}" })
                author.text = event.author
                published.text = dateUtcToString(event.published)
                content.text = event.content
                like.isChecked = event.likedByMe
                like.text = event.likeOwnerIds?.size.toString()

                like.setOnClickListener {
                    viewModel.likeById(event)
                    if (like.isChecked) {
                        like.text = (like.text.toString().toInt() + 1).toString()
                        return@setOnClickListener
                    } else {
                        like.text = (like.text.toString().toInt() - 1).toString()
                        return@setOnClickListener
                    }


                }
                attachmentImage.isVisible = event.attachment?.type == AttachmentType.IMAGE
                event.attachment?.let { attachmentImage.loadAttachmentView(it.url) }
                videoContainer.isVisible = event.attachment?.type == AttachmentType.VIDEO
                attachmentVideo.apply {
                    if (event.attachment?.type == AttachmentType.VIDEO && !event.attachment.url.isNullOrBlank()) {
                        setVideoURI(
                            Uri.parse(event.attachment?.url)
                        )
                        setOnPreparedListener {
                            seekTo(5)
                        }

                        playVideoButton.setOnClickListener {
                            viewModel.clearPlayAudio()
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
                    if (playAudioButton.isChecked) {
                        event.attachment?.url?.let { url -> MediaLifecycleObserver.mediaPlay(url) }
                    } else {
                        event.attachment?.url?.let { MediaLifecycleObserver.mediaStop() }
                    }

                }
                link.isVisible = event.link?.isEmpty() == false
                link.text = event.link
                participantTitle.isVisible = true
                iconParticipant.isVisible = true
                participants.isVisible = true
                scrollParticipant.isVisible = true
                participantCount.isVisible = true
                participantCount.text = event.participantsIds.size.toString()

                speakerTitle.isVisible = true
                iconSpeaker.isVisible = true
                speakers.isVisible = true
                scrollSpeakers.isVisible = true
                speakersCount.isVisible = true
                speakersCount.text = event.speakerIds.size.toString()

                job.isVisible = true
                jobCompany.isVisible = true
                jobCompany.text = event.authorJob ?: getString(R.string.looking_for_best_company)

                event.speakerIds.forEach { id ->
                    event.users.map { user ->
                        if (user.key.toInt() == id) {
                            val speaker =
                                layoutInflater.inflate(
                                    R.layout.card_mentioned,
                                    speakers,
                                    false
                                )
                            speaker.findViewById<ImageView>(R.id.avatarView)
                                .loadAvatar(user.value.avatar)
                            speaker.findViewById<TextView>(R.id.userName).text =
                                user.value.name.toString()
                            speakers.addView(speaker)
                        }
                    }
                }

                event.participantsIds.forEach { id ->
                    event.users.map { user ->
                        if (user.key.toInt() == id) {
                            val participant =
                                layoutInflater.inflate(
                                    R.layout.card_mentioned,
                                    participants,
                                    false
                                )
                            participant.findViewById<ImageView>(R.id.avatarView)
                                .loadAvatar(user.value.avatar)
                            participant.findViewById<TextView>(R.id.userName).text =
                                user.value.name.toString()
                            participants.addView(participant)
                        }
                    }
                }

                typeOfEvent.text = event.type.toString()
                dateOfEvent.text = dateUtcToString(event.datetime)

                menu.isVisible = event.ownedByMe
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    viewModel.removeEventById(event.id)
                                    findNavController().navigateUp()
                                    true
                                }

                                R.id.editContent -> {
                                    viewModel.updateAttachment(
                                        url = event.attachment?.url,
                                        attachmentType = event.attachment?.type,
                                        uri = event.attachment?.url?.toUri(),
                                        file = null
                                    )
                                    userViewModel.setCheckedUsers(event.speakerIds)
                                    viewModel.edit(event)
                                    viewModel.clearPlayAudio()
                                    findNavController().navigate(R.id.newEventFragment)
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }

            }
        }







        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_close_full_screen_view, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.close) {
                        findNavController().navigateUp()
                        return true
                    } else {
                        return false
                    }
                }
            },
            viewLifecycleOwner,
        )



        return binding.root
    }
}