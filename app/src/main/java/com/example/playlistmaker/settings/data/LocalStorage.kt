package com.example.playlistmaker.settings.data

interface LocalStorage {
    fun switch(darkThemeEnabled: Boolean)
    fun isDarkModeOn(): Boolean
}