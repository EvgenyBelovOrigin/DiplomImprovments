package ru.netology.nework.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nework.R


fun ImageView.loadAvatar(url: String?) {
    Glide.with(
        this
    )
        .load(url)
        .error(R.drawable.ic_error_100dp)
        .placeholder(R.drawable.ic_loading_100dp)
        .timeout(15_000)
        .circleCrop()
        .into(this)
}

fun ImageView.loadAttachmentView(url: String) {
    Glide.with(this)
        .load(url)
        .error(R.drawable.ic_error_100dp)
        .placeholder(R.drawable.ic_loading_100dp)
        .timeout(15_000)
        .into(this)
}