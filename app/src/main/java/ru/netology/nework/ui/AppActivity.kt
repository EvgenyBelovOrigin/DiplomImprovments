package ru.netology.nework.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}