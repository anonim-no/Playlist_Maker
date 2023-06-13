package com.example.playlistmaker.settings.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.PLAYLIST_MAKER_PREFERENCE
import com.example.playlistmaker.settings.data.LocalStorage
import com.example.playlistmaker.settings.data.local.ThemeStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsDataModule = module {
    single<LocalStorage>{
        ThemeStorage(sharedPreferences = get())
    }
    single<SharedPreferences> {
        androidContext().getSharedPreferences(PLAYLIST_MAKER_PREFERENCE, Context.MODE_PRIVATE)
    }
}