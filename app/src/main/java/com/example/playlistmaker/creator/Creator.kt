package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.THEME_PREFERENCES
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.data.ThemeSwitchRepositoryImpl
import com.example.playlistmaker.settings.data.local.ThemeStorage
import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor
import com.example.playlistmaker.settings.domain.api.ThemeSwitchRepository
import com.example.playlistmaker.settings.domain.impl.ThemeSwitchInteractorImpl

object Creator {

    private fun getSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository(context))
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