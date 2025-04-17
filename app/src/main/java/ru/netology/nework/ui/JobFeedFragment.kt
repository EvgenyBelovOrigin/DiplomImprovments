package ru.netology.nework.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.JobAdapter
import ru.netology.nework.adapter.JobOnInteractionListener
import ru.netology.nework.databinding.FragmentFeedJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.utils.StringArg
import ru.netology.nework.viewmodel.JobViewModel
import ru.netology.nework.viewmodel.WallViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject


@AndroidEntryPoint
class JobFeedFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: JobViewModel by activityViewModels()
    private val wallViewModel: WallViewModel by activityViewModels()


    companion object {
        var Bundle.textArg: String? by StringArg
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding = FragmentFeedJobBinding.inflate(inflater, container, false)
        binding.fab.isGone = appAuth.authState.value?.id != wallViewModel.user.value?.id

        val adapter = JobAdapter(
            object : JobOnInteractionListener {
                override fun onDeleteJob(job: Job) {
                    viewModel.deleteJob(job)
                }

            }
        )
        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        binding.swipeRefresh.setOnRefreshListener {

//                viewModel.getAllJobs()
            binding.swipeRefresh.isRefreshing = false
        }
        viewModel.onError.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_loading)
                .setPositiveButton(R.string.ok, null)
                .show()
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
