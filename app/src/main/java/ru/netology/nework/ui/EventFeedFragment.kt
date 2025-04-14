package ru.netology.nework.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
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
import ru.netology.nework.adapter.EventsAdapter
import ru.netology.nework.adapter.EventsOnInteractionListener
import ru.netology.nework.databinding.FragmentFeedEventBinding
import ru.netology.nework.dto.Event
import ru.netology.nework.viewmodel.EventViewModel
import ru.netology.nework.viewmodel.UserViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject


@AndroidEntryPoint
class EventFeedFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: EventViewModel by activityViewModels()

    private val userViewModel: UserViewModel by activityViewModels()


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedEventBinding.inflate(inflater, container, false)
        binding.bottomNavigation.selectedItemId = R.id.events
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.posts -> {
                    findNavController().navigate(R.id.postFeedFragment)
                    true
                }

                R.id.events -> {
                    true
                }

                R.id.users -> {
                    findNavController().navigate(R.id.userFeedFragment)
                    true
                }

                else -> false
            }
        }
        val adapter = EventsAdapter(object : EventsOnInteractionListener {
            override fun onEdit(event: Event, position: Int) {
                viewModel.updateAttachment(
                    url = event.attachment?.url,
                    attachmentType = event.attachment?.type,
                    uri = event.attachment?.url?.toUri(),
                    file = null
                )

                userViewModel.setCheckedUsers(event.speakerIds)
                viewModel.edit(event)
                viewModel.clearPlayAudio()
                findNavController().navigate(R.id.newEventFragment)
            }

            override fun onRemove(event: Event, position: Int) {
                viewModel.removeEventById(event.id)
            }

            override fun onPlayAudio(event: Event, position: Int) {
                viewModel.playAudio(event)
            }

            override fun onStopAudio() {
                viewModel.clearPlayAudio()
            }

            override fun onLike(event: Event, position: Int) {
                viewModel.likeById(event)
            }

            override fun onItemClick(event: Event, position: Int) {
                viewModel.edit(event)
                viewModel.clearPlayAudio()
                findNavController().navigate(R.id.detailEventFragment)
            }

            override fun onVideoPlay(position: Int) {
            }

        })
        viewModel.clearPlayAudio()
        binding.list.adapter = adapter

        viewModel.clearEdited()


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

            viewModel.clearPlayAudio()
            adapter.refresh()

        }
        viewModel.onLikeError.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_like)
                .setPositiveButton(R.string.ok, null)
                .show()
        }

        viewModel.onDeleteError.observe(viewLifecycleOwner) {
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
                viewModel.clearAttachment()
                viewModel.clearEdited()
                findNavController().navigate(R.id.newEventFragment)
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
