package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentSignUpBinding
import ru.netology.nework.viewmodel.SignUpViewModel


@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private val viewModel: SignUpViewModel by viewModels()

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
                    viewModel.updateAvatar(uri, uri.toFile())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
            binding.login.requestFocus()
        }
        binding.signUp.setOnClickListener {
            when {
                binding.login.text.toString().isEmpty() -> {
                    binding.login.error =
                        getString(R.string.login_can_not_be_empty)
                    return@setOnClickListener
                }

                binding.name.text.toString().isEmpty() -> {
                    binding.name.error = getString(R.string.name_can_not_be_empty)
                    return@setOnClickListener
                }

                binding.pass.text.toString().isEmpty() -> {
                    binding.pass.error =
                        getString(R.string.pass_can_not_be_empty)
                    return@setOnClickListener
                }

                binding.passConfirm.text.toString().isEmpty() -> {
                    binding.passConfirm.error = getString(R.string.pass_confirm_can_not_be_empty)
                    return@setOnClickListener
                }

            }
            binding.progress.isVisible = true
            viewModel.signUp(
                binding.login.text.toString(),
                binding.pass.text.toString(),
                binding.passConfirm.text.toString(),
                binding.name.text.toString()
            )


        }
        viewModel.signedUp.observe(viewLifecycleOwner) {
            binding.progress.isVisible = false
            findNavController().navigateUp()
        }
        viewModel.wrongPassConfirm.observe(viewLifecycleOwner) {
            binding.signUpFrame.clearFocus()
            binding.progress.isVisible = false
            binding.error.isVisible = true
        }
        viewModel.exception.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_user_exist)
                .setPositiveButton(R.string.ok) {
                        _, _,
                    ->
                    findNavController().navigateUp()
                }
                .show()
        }

        viewModel.avatar.observe(viewLifecycleOwner) { photo ->
            if (photo.uri == null) {
//                binding.avatar.isGone = true
                return@observe
            }
//            binding.photo.isVisible = true
            binding.avatar.setImageURI(photo.uri)
            binding.recycle.isVisible = true

        }

        binding.login.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }
        binding.pass.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }
        binding.passConfirm.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false

        }
        binding.name.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }

        binding.avatar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_source)
                .setMessage(R.string.choose_source_message)
                .setPositiveButton(R.string.selfie) {
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

        binding.recycle.setOnClickListener {
            viewModel.clearAvatar()
            binding.recycle.isVisible = false
            binding.avatar.setImageResource(R.drawable.monogram)
        }


        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(
                        R.menu.menu_close_full_screen_view,
                        menu
                    )
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