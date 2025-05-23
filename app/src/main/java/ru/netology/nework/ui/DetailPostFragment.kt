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
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.utils.loadAvatar
import ru.netology.nework.viewmodel.PostViewModel
import ru.netology.nework.viewmodel.UserViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailPostFragment : Fragment() {


    private val viewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )


        viewModel.edited.observe(viewLifecycleOwner) { post ->
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
                    viewModel.likeById(post)
                    if (like.isChecked) {
                        like.text = (like.text.toString().toInt() + 1).toString()
                        return@setOnClickListener
                    } else {
                        like.text = (like.text.toString().toInt() - 1).toString()
                        return@setOnClickListener
                    }


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

                attachmentAudioLayout.isVisible = post.attachment?.type == AttachmentType.AUDIO
                playAudioButton.isChecked = post.isPlayingAudio
                playAudioButton.setOnClickListener {
                    if (playAudioButton.isChecked) {
                        post.attachment?.url?.let { url -> MediaLifecycleObserver.mediaPlay(url) }
                    } else {
                        post.attachment?.url?.let { MediaLifecycleObserver.mediaStop() }
                    }

                }
                link.isVisible = post.link?.isEmpty() == false
                link.text = post.link
                mentionedTitle.isVisible = true
                iconMentioned.isVisible = true
                mentioned.isVisible = true
                scrollMentioned.isVisible = true
                mentionedCount.isVisible = true
                mentionedCount.text = post.mentionIds?.size.toString()
                job.isVisible = true
                jobCompany.isVisible = true
                jobCompany.text = post.authorJob ?: getString(R.string.looking_for_best_company)

                if (post.users !== null) {
                    post.mentionIds?.forEach { id ->
                        post.users.map { user ->
                            if (user.key.toInt() == id) {
                                val mentionedPeople =
                                    layoutInflater.inflate(
                                        R.layout.card_mentioned,
                                        mentioned,
                                        false
                                    )
                                mentionedPeople.findViewById<ImageView>(R.id.avatarView)
                                    .loadAvatar(user.value?.avatar)
                                mentionedPeople.findViewById<TextView>(R.id.userName).text =
                                    user.value?.name.toString()
                                mentioned.addView(mentionedPeople)
                            }
                        }
                    }
                }


                menu.isVisible = post.ownedByMe
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    viewModel.removePostById(post.id)
                                    findNavController().navigateUp()
                                    true
                                }

                                R.id.editContent -> {
                                    viewModel.updateAttachment(
                                        url = post.attachment?.url,
                                        attachmentType = post.attachment?.type,
                                        uri = post.attachment?.url?.toUri(),
                                        file = null
                                    )
                                    post.mentionIds?.let { mentionIds ->
                                        userViewModel.setCheckedUsers(
                                            mentionIds
                                        )
                                    }
                                    viewModel.edit(post)
                                    findNavController().navigate(R.id.newPostFragment)
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