package ru.netology.nework.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import ru.netology.nework.R
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object AndroidUtils {

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInputFromInputMethod(view.windowToken, 0)
    }

}