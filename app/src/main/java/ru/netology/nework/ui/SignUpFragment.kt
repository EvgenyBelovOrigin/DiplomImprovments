package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentSignUpBinding


@AndroidEntryPoint
class SignUpFragment : Fragment() {
//    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )
//        val imagePickerLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == ImagePicker.RESULT_ERROR) {
//                    Snackbar.make(
//                        binding.root,
//                        ImagePicker.getError(it.data),
//                        Snackbar.LENGTH_LONG
//                    ).show()
//                } else {
//                    val uri = it.data?.data ?: return@registerForActivityResult
//                    viewModel.updateAvatar(uri, uri.toFile())
//                }
//            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
            binding.loginEdit.requestFocus()
        }
//        binding.signUpButton.setOnClickListener {
//            binding.progress.isVisible = true
//            viewModel.signUp(
//                binding.loginEdit.text.toString(),
//                binding.passEdit.text.toString(),
//                binding.passConfirmEdit.text.toString(),
//                binding.nameEdit.text.toString()
//            )
//
//
//        }
//        viewModel.signedUp.observe(viewLifecycleOwner) {
//
//            binding.progress.isVisible = false
//            findNavController().navigateUp()
//        }
//        viewModel.wrongPassConfirm.observe(viewLifecycleOwner) {
//            binding.signUpFrame.clearFocus()
//            binding.progress.isVisible = false
//            binding.error.isVisible = true
//        }
//        viewModel.exception.observe(viewLifecycleOwner) {
//            MaterialAlertDialogBuilder(requireContext())
//                .setTitle(R.string.error)
//                .setMessage(R.string.error_loading)
//                .setPositiveButton(R.string.ok) {
//                        _, _,
//                    ->
//                    findNavController().navigateUp()
//                }
//                .show()
//        }

//        viewModel.avatar.observe(viewLifecycleOwner) { photo ->
//            if (photo.uri == null) {
//                binding.avatar.isGone = true
//                return@observe
//            }
//            binding.avatar.isVisible = true
//            binding.avatar.setImageURI(photo.uri)
//        }

        binding.loginEdit.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }
        binding.passEdit.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }
        binding.passConfirmEdit.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false

        }
        binding.nameEdit.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }

//        binding.takeAvatar.setOnClickListener {
//            ImagePicker.with(this)
//                .crop()
//                .compress(2048)
//                .cameraOnly()
//                .createIntent {
//                    imagePickerLauncher.launch(it)
//                }
//        }
//        binding.pickAvatar.setOnClickListener {
//            ImagePicker.with(this)
//                .crop()
//                .compress(2048)
//                .galleryOnly()
//                .galleryMimeTypes(
//                    arrayOf(
//                        "image/png",
//                    )
//                )
//                .createIntent(imagePickerLauncher::launch)
//
//        }
//        binding.avatar.setOnClickListener {
//            viewModel.clearAvatar()
//        }



        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(ru.netology.nework.R.menu.menu_close_full_screen_view, menu)
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