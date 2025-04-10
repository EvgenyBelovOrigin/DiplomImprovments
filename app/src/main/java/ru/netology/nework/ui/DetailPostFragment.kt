package ru.netology.nework.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.databinding.FragmentNewPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.AndroidUtils.getFile
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.StringArg
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.utils.loadAvatar
import ru.netology.nework.viewmodel.PostViewModel
import java.io.FileNotFoundException
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailPostFragment : Fragment() {


    private val viewModel: PostViewModel by activityViewModels()

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
                        like.text = (like.text.toString().toInt()+1).toString()
                        return@setOnClickListener
                    }else{
                        like.text = (like.text.toString().toInt()-1).toString()
                        return@setOnClickListener
                    }


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
                    viewModel.playAudio(post)
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