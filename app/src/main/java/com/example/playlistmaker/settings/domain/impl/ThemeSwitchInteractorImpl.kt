package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor
import com.example.playlistmaker.settings.domain.api.ThemeSwitchRepository

class ThemeSwitchInteractorImpl(private val themeSwitchRepository: ThemeSwitchRepository) :
    ThemeSwitchInteractor {
    override fun switch(isDarkModeOn: Boolean) {
        themeSwitchRepository.switchTheme(isDarkModeOn)
    }

    override fun isDarkModeOn(): Boolean {
        return themeSwitchRepository.isDarkModeOn()
    }

    override fun applyCurrentTheme() {
        themeSwitchRepository.applyCurrentTheme()
    }
}