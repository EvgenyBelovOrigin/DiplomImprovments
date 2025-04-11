package ru.netology.nework.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.adapter.UsersAdapter
import ru.netology.nework.adapter.UsersOnInteractionListener
import ru.netology.nework.databinding.FragmentFeedUserBinding
import ru.netology.nework.dto.User
import ru.netology.nework.viewmodel.UserViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject


@AndroidEntryPoint
class UserFeedFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: UserViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding = FragmentFeedUserBinding.inflate(inflater, container, false)


        binding.bottomNavigation.selectedItemId = R.id.users
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.posts -> {
                    findNavController().navigate(R.id.postFeedFragment)
                    true
                }

                R.id.events -> {
                    findNavController().navigate(R.id.eventFeedFragment)
                    true
                }

                R.id.users -> {
                    true
                }

                else -> false
            }
        }
        val adapter = UsersAdapter(object : UsersOnInteractionListener {
            override fun onEdit(user: User, position: Int) {
            }


        })
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllUsers()

        }
        viewModel.onError.observe(viewLifecycleOwner){
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_loading)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
        viewModel.onStartLoading.observe(viewLifecycleOwner){
            binding.swipeRefresh.isRefreshing = true
        }
        viewModel.onStopLoading.observe(viewLifecycleOwner){
            binding.swipeRefresh.isRefreshing = false
        }

        return binding.root
    }

    private fun requestSignIn() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.requestSignInTitle)
            .setMessage(R.string.requestSignInMessage)
            .setPositiveButton(R.string.ok) {
                    _, _,
                ->
                findNavController().navigate(R.id.signInFragment)
            }
            .setNegativeButton(R.string.return_to_posts, null)
            .show()
    }

}
