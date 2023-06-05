package com.example.playlistmaker.settings.domain.api

interface ThemeSwitchInteractor {
    fun switch(isDarkModeOn: Boolean)
    fun isDarkModeOn(): Boolean
    fun applyCurrentTheme()
}