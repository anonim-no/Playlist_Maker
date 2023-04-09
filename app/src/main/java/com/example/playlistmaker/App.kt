package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

const val PLAYLIST_MAKER_PREFERENCE = "playlist_maker_preference"
const val DARK_THEME_PREFERENCE = "dark_theme_preference"
const val TRACK = "track"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs: SharedPreferences =
            getSharedPreferences(PLAYLIST_MAKER_PREFERENCE, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_PREFERENCE, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {

        if (darkTheme != darkThemeEnabled) {
            darkTheme = darkThemeEnabled
            val sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCE, MODE_PRIVATE)
            sharedPreferences.edit { putBoolean(DARK_THEME_PREFERENCE, darkTheme) }
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}