package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor
import com.example.playlistmaker.settings.domain.impl.ThemeSwitchInteractorImpl
import org.koin.dsl.module

val settingsInteractorModule = module {
    single<ThemeSwitchInteractor>{
        ThemeSwitchInteractorImpl(themeSwitchRepository = get())
    }
}