package ru.netology.nework.ui

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
import ru.netology.nework.adapter.OnInteractionListener
import ru.netology.nework.adapter.PostsAdapter
import ru.netology.nework.databinding.FragmentFeedPostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.PostViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject


@AndroidEntryPoint
class PostFeedFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedPostBinding.inflate(inflater, container, false)
        binding.bottomNavigation.selectedItemId = R.id.posts
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.posts -> {
                    true
                }

                R.id.events -> {
                    findNavController().navigate(R.id.eventFeedFragment)
                    true
                }

                else -> false
            }
        }
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {}
            override fun onRemove(post: Post) {
                viewModel.removePostById(post.id)
            }
            override fun onPlayAudio(post: Post) {
                viewModel.playAudio(post)
            }

            override fun onStopAudio() {
                viewModel.clearPlayAudio()
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

        })
        viewModel.clearPlayAudio()
        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }
        viewModel.refreshAdapter.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                appAuth.authState.collectLatest {
                    viewModel.daoClearAll()
                    adapter.refresh()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swiperefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }
        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
            viewModel.clearPlayAudio()
        }
        viewModel.onLikeError.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_like)
                .setPositiveButton(R.string.ok, null)
                .show()
        }

        viewModel.onDeleteError.observe(viewLifecycleOwner){
            Toast.makeText(
                activity,
                R.string.error_delete,
                Toast.LENGTH_LONG
            ).show()
        }
        viewModel.requestSignIn.observe(viewLifecycleOwner) {
            requestSignIn()
        }

        binding.fab.setOnClickListener {
            if (appAuth.authState.value?.id == 0) {
                requestSignIn()
            } else {
                findNavController().navigate(R.id.newPostFragment)
            }
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
