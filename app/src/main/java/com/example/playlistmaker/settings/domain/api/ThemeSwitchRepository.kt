package com.example.playlistmaker.settings.domain.api

interface ThemeSwitchRepository {
    fun switchTheme(darkThemeEnabled: Boolean)
    fun isDarkModeOn(): Boolean
    fun applyCurrentTheme()
}