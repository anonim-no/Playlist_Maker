package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor

const val THEME_PREFERENCES = "theme_preferences"
const val DARK_MODE = "dark_mode"

const val PLAYLIST_MAKER_PREFERENCE = "playlist_maker_preference"
const val TRACK = "track"

class App : Application() {

    lateinit var themeSwitcherInteractor: ThemeSwitchInteractor

    override fun onCreate() {
        super.onCreate()
        themeSwitcherInteractor = Creator.provideThemeSwitchInteractor(this)
        themeSwitcherInteractor.applyCurrentTheme()
    }

}