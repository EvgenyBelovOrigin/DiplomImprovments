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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentSignInBinding
import ru.netology.nework.viewmodel.SignInViewModel


@AndroidEntryPoint
class SignInFragment : Fragment() {
    private val viewModel: SignInViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
            binding.login.requestFocus()
        }

        binding.signIn.setOnClickListener {
            if(binding.login.text.toString().isEmpty()){
                binding.login.error = getString(R.string.login_can_not_be_empty)
                return@setOnClickListener
            }
            if(binding.pass.text.toString().isEmpty()){
                binding.pass.error = getString(R.string.pass_can_not_be_empty)
                return@setOnClickListener
            }
            binding.progress.isVisible = true
            viewModel.signIn(binding.login.text.toString(), binding.pass.text.toString())
            binding.signInFrame.clearFocus()
        }
        viewModel.signedIn.observe(viewLifecycleOwner) {
            binding.progress.isVisible = false
            findNavController().navigateUp()
        }
        viewModel.notFoundException.observe(viewLifecycleOwner) {
            binding.progress.isVisible = false
            binding.error.isVisible = true
        }
        viewModel.exception.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_loading)
                .setPositiveButton(R.string.ok) {
                        _, _,
                    ->
                    findNavController().navigateUp()
                }
                .show()
        }

        binding.login.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }
        binding.pass.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }
        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
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