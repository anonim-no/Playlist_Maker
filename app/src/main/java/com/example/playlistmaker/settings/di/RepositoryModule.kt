package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.data.ThemeSwitchRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeSwitchRepository
import org.koin.dsl.module

val settingsRepositoryModule = module {
    single<ThemeSwitchRepository>{
        ThemeSwitchRepositoryImpl(themeStorage = get())
    }
}