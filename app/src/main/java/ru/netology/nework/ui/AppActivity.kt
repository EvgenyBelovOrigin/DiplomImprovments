package ru.netology.nework.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_menu, menu)
                    menu.setGroupVisible(R.id.authenticated, false)
                    menu.setGroupVisible(R.id.unauthenticated, true)
//                    viewModel.data.observe(this@AppActivity) {
//                        menu.setGroupVisible(R.id.authenticated, viewModel.authenticated)
//                        menu.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
//
//                    }
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

//                        R.id.signout -> {
////                            appAuth.clear()
//                            true
//                        }

                        else -> false
                    }


            }
        )
    }
}