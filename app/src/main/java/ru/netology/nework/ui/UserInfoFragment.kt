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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
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




        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()


        }
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_close_full_screen_view, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.close) {
                        viewModel.clearPlayAudio()
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