package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentFeedEventBinding
import ru.netology.nework.databinding.FragmentFeedPostBinding


@AndroidEntryPoint
class EventFeedFragment : Fragment() {

//    @Inject
//    lateinit var appAuth: AppAuth
//    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedEventBinding.inflate(inflater, container, false)




//        binding.fab.setOnClickListener {
//            if (appAuth.authState.value?.id == 0L) {
//                requestSignIn()
//            } else {
//                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
//            }
//        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                adapter.loadStateFlow.collectLatest { state ->
//                    binding.swiperefresh.isRefreshing =
//                        state.refresh is LoadState.Loading
//                }
//            }
//        }
//        binding.swiperefresh.setOnRefreshListener {
//            adapter.refresh()
//        }
//        viewModel.onLikeError.observe(viewLifecycleOwner) { id ->
//            MaterialAlertDialogBuilder(requireContext())
//                .setTitle(R.string.error)
//                .setMessage(R.string.error_like)
//                .setPositiveButton(R.string.ok, null)
//                .show()
//        }
//        viewModel.requestSignIn.observe(viewLifecycleOwner) {
//            requestSignIn()
//        }

        return binding.root
    }

//    fun requestSignIn() {
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle(R.string.requestSignInTitle)
//            .setMessage(R.string.requestSignInMessage)
//            .setPositiveButton(R.string.ok) {
//                    _, _,
//                ->
//                findNavController().navigate(R.id.signInFragment)
//            }
//            .setNegativeButton(R.string.return_to_posts, null)
//            .show()
//    }
}
