package ru.netology.nework.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.UsersAdapter
import ru.netology.nework.adapter.UsersOnInteractionListener
import ru.netology.nework.databinding.FragmentFeedUserBinding
import ru.netology.nework.dto.User
import ru.netology.nework.utils.StringArg
import ru.netology.nework.viewmodel.UserViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject


@AndroidEntryPoint
class UserFeedFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: UserViewModel by activityViewModels()

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding = FragmentFeedUserBinding.inflate(inflater, container, false)
        val needToCheckUsers = arguments?.textArg == getString(R.string.check_users)
        if (!needToCheckUsers) {
            binding.bottomNavigationLayout.isVisible = true
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
        }
        if (needToCheckUsers) {
            requireActivity().addMenuProvider(
                object : MenuProvider {
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        menuInflater.inflate(R.menu.menu_new_post, menu)
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                        findNavController().navigateUp()
                        //TODO - changes in db
                        return true
                    }
                },
                viewLifecycleOwner,
            )
        }

        val adapter = UsersAdapter(
            object : UsersOnInteractionListener {
                override fun onCheckUser(user: User, position: Int) {
                    viewModel.checkUser(user)
                }

                override fun onChooseUser(user: User, position: Int) {
                    findNavController().navigate(R.id.wallFeedFragment,
                        Bundle().apply {
                            textArg = user.id.toString()
                        })
                }


            },
            needToCheckUsers
        )
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        binding.swipeRefresh.setOnRefreshListener {
            if (!needToCheckUsers)
                viewModel.getAllUsers()
            binding.swipeRefresh.isRefreshing = false
        }
        viewModel.onError.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_loading)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
        viewModel.onStartLoading.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = true
        }
        viewModel.onStopLoading.observe(viewLifecycleOwner) {
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
