package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.data.ThemeSwitchRepositoryImpl
import com.example.playlistmaker.settings.data.local.ThemeStorage
import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor
import com.example.playlistmaker.settings.domain.api.ThemeSwitchRepository
import com.example.playlistmaker.settings.domain.impl.ThemeSwitchInteractorImpl

object Creator {

    private const val LOCAL_STORAGE = "local_storage"
    private const val THEME_PREFERENCES = "current_theme"

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getThemeSwitchRepository(context: Context): ThemeSwitchRepository {
        return ThemeSwitchRepositoryImpl(
            ThemeStorage(context.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE))
        )
    }
    fun provideThemeSwitchInteractor(context: Context): ThemeSwitchInteractor {
        return ThemeSwitchInteractorImpl(getThemeSwitchRepository(context))
    }
}