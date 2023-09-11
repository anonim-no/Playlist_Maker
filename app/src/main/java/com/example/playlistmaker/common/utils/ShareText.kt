package com.example.playlistmaker.common.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.playlistmaker.R

fun shareText(text: String, context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.type = "text/plain"
    try {
        context.startActivity(intent)
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.settings_not_found_app),
            Toast.LENGTH_SHORT
        ).show()
    }
}