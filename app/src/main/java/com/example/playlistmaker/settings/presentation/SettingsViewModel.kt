package com.example.playlistmaker.settings.presentation

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor

class SettingsViewModel(private val switchThemeInteractor: ThemeSwitchInteractor) : ViewModel() {

    fun switchTheme(isChecked: Boolean) {
        switchThemeInteractor.switch(isChecked)
    }

    fun isDarkThemeOn(): Boolean {
        return switchThemeInteractor.isDarkModeOn()
    }

}