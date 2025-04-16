package ru.netology.nework.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.ui.UserFeedFragment.Companion.textArg
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_menu, menu)

                    viewModel.data.observe(this@AppActivity) {
                        menu.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                        menu.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)

                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.signin -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.signInFragment)
                            true
                        }

                        R.id.signup -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.signUpFragment)
                            true
                        }

                        R.id.signout -> {
                            appAuth.clear()
                            true
                        }

                        R.id.myWall -> {
                            findNavController(R.id.nav_host_fragment).navigate(
                                R.id.userInfoFragment,
                                Bundle().apply {
                                    textArg = appAuth.authState.value?.id.toString()
                                })
                            true
                        }

                        else -> false
                    }


            }
        )
    }
}