package ru.netology.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.adapter.FragmentPageAdapter
import ru.netology.nework.databinding.FragmentUserInfoBinding
import ru.netology.nework.utils.StringArg
import ru.netology.nework.utils.loadAvatar
import ru.netology.nework.viewmodel.UserViewModel
import ru.netology.nework.viewmodel.WallViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

@AndroidEntryPoint
class UserInfoFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: WallViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: FragmentPageAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentUserInfoBinding.inflate(
            inflater,
            container,
            false
        )
        val authorId = arguments?.textArg?.toInt()
        val user = userViewModel.chooseUser(authorId)
        binding.avatar.loadAvatar(user?.avatar.toString())
        binding.collapsingToolbar.title = user?.name
        if (user != null) {
            viewModel.setUser(user)
        }

        setupWithNavController(
            binding.toolBar, findNavController(), AppBarConfiguration(findNavController().graph)
        )
        tabLayout = binding.tabs
        viewPager2 = binding.viewPager
        adapter = FragmentPageAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, index ->
            when (index) {
                0 -> {
                    tab.text = getString(R.string.wall)
                }

                1 -> {
                    tab.text = getString(R.string.job)
                }

                else -> Unit
            }

        }.attach()


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()


        }



        return binding.root
    }
}