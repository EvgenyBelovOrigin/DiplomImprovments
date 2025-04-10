package ru.netology.nework.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
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
import ru.netology.nework.databinding.FragmentNewPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.AndroidUtils.getFile
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.StringArg
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.viewmodel.PostViewModel
import java.io.FileNotFoundException
import java.io.IOException

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        const val MAX_SIZE = 15728640
    }

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )


        binding.editContent.setText(viewModel.edited.value?.content)
        arguments?.textArg
            ?.let(binding.editContent::setText)

        binding.editContent.requestFocus()

        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == ImagePicker.RESULT_ERROR) {
                    Snackbar.make(
                        binding.root,
                        ImagePicker.getError(it.data),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    val uri = it.data?.data ?: return@registerForActivityResult
                    viewModel.updateAttachment(null, uri, uri.toFile(), AttachmentType.IMAGE)
                }
            }
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                try {
                    val fileDescriptor =
                        uri?.let {
                            requireContext().contentResolver.openAssetFileDescriptor(
                                it,
                                "r"
                            )
                        }
                    val fileSize = fileDescriptor?.length ?: 0
                    if (fileSize > MAX_SIZE) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.attachment_too_large),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@registerForActivityResult
                    }
                    fileDescriptor?.close()
                    val file = uri?.getFile(requireContext())
                    if (uri?.let {
                            requireContext().contentResolver.getType(it)
                                ?.startsWith("audio/")
                        } == true
                    ) {
                        viewModel.updateAttachment(null, uri, file, AttachmentType.AUDIO)
                    } else {
                        if (uri?.let {
                                requireContext().contentResolver.getType(it)
                                    ?.startsWith("video/")
                            } == true
                        ) {
                            viewModel.updateAttachment(null, uri, file, AttachmentType.VIDEO)
                        }
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        viewModel.attachment.observe(viewLifecycleOwner) { attachment ->
            if (attachment.attachmentType == null) {
                binding.photoContainer.isGone = true
                binding.audioContainer.isGone = true
                binding.videoContainer.isGone = true
                return@observe
            }
            when (attachment.attachmentType) {
                AttachmentType.IMAGE -> {
                    binding.photoContainer.isVisible = true
                    binding.videoContainer.isGone = true
                    binding.audioContainer.isGone = true
                    if (attachment.file !== null) {
                        binding.photo.setImageURI(attachment.uri)
                    }else{
                        binding.photo.loadAttachmentView(attachment.uri.toString())//todo why?
                    }

                }

                AttachmentType.VIDEO -> {
                    binding.videoContainer.isVisible = true
                    binding.audioContainer.isGone = true
                    binding.photoContainer.isGone = true
                    binding.attachmentVideo.apply {

                        setVideoURI(
                            Uri.parse(attachment.uri.toString())
                        )

                        setOnPreparedListener {
                            seekTo(5)
                            binding.playVideoButton.isVisible = true
                        }
                        binding.playVideoButton.setOnClickListener {
                            start()
                            binding.playVideoButton.isVisible = false
                            setOnCompletionListener {
                                resume()
                                binding.playVideoButton.isVisible = true
                            }
                        }
                        binding.attachmentVideo.setOnClickListener {
                            pause()
                            binding.playVideoButton.isVisible = true
                        }
                    }
                }

                AttachmentType.AUDIO -> {
                    binding.audioContainer.isVisible = true
                    binding.photoContainer.isGone = true
                    binding.videoContainer.isGone = true
                    binding.playAudioButton.setOnClickListener {
                        if (binding.playAudioButton.isChecked) {
                            if (attachment.file != null) {
                                attachment.file.let { file ->
                                    MediaLifecycleObserver.mediaPlay(file.path)
                                }

                            } else {
                                attachment.uri?.let { uri ->
                                    MediaLifecycleObserver.mediaPlay(uri.toString())
                                }//todo WHY???
                            }
                        } else {
                            MediaLifecycleObserver.mediaStop()
                        }
                    }
                }

                null -> error("Unknown attachment type")
            }

        }

        binding.removePhoto.setOnClickListener {
            viewModel.clearAttachment()
        }
        binding.removeAudio.setOnClickListener {
            viewModel.clearAttachment()
        }
        binding.removeVideo.setOnClickListener {
            viewModel.clearAttachment()
        }

        binding.takePhoto.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_source)
                .setMessage(R.string.choose_source_message_attachment)
                .setPositiveButton(R.string.photo) {
                        _, _,
                    ->
                    ImagePicker.with(this)
                        .crop()
                        .compress(2048)
                        .cameraOnly()
                        .createIntent {
                            imagePickerLauncher.launch(it)
                        }

                }
                .setNegativeButton(R.string.image) {
                        _, _,
                    ->
                    ImagePicker.with(this)
                        .cropSquare()
                        .compress(2048)
                        .galleryOnly()
                        .galleryMimeTypes(
                            arrayOf(
                                "image/png",
                                "image/jpeg"
                            )
                        )
                        .createIntent(imagePickerLauncher::launch)

                }
                .show()
        }
        binding.takeFile.setOnClickListener {
            val choose = arrayOf("audio/*", "video/*")
            resultLauncher.launch(choose)
        }


        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_new_post, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.save) {
                        viewModel.changeContent(
                            binding.editContent.text.toString(),
                            binding.link.text.toString()
                        )
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        return true
                    } else {
                        return false
                    }
                }
            },
            viewLifecycleOwner,
        )

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        return binding.root
    }
}