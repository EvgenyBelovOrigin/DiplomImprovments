package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
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
import ru.netology.nework.utils.StringArg
import ru.netology.nework.viewmodel.PostViewModel

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
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


        binding.edit.setText(viewModel.edited.value?.content)
        arguments?.textArg
            ?.let(binding.edit::setText)

        binding.edit.requestFocus()

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
        viewModel.attachment.observe(viewLifecycleOwner) { photo ->
            if (photo.uri == null) {
                binding.photoContainer.isGone = true
                return@observe
            }
            binding.photoContainer.isVisible = true
            binding.photo.setImageURI(photo.uri)
        }
        binding.removePhoto.setOnClickListener {
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


        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_new_post, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.save) {
                        viewModel.changeContent(binding.edit.text.toString())
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