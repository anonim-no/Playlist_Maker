package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.PLAYLIST_MAKER_PREFERENCE
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.data.local.SharedPreferencesClient
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
        return SearchRepositoryImpl(
            RetrofitNetworkClient(context),
            SharedPreferencesClient(
                context.getSharedPreferences(
                    PLAYLIST_MAKER_PREFERENCE,
                    Context.MODE_PRIVATE
                )
            )
        )
    }
    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository(context))
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    private fun getThemeSwitchRepository(context: Context): ThemeSwitchRepository {
        return ThemeSwitchRepositoryImpl(
            ThemeStorage(
                context.getSharedPreferences(
                    PLAYLIST_MAKER_PREFERENCE,
                    Context.MODE_PRIVATE
                )
            )
        )
    }
    fun provideThemeSwitchInteractor(context: Context): ThemeSwitchInteractor {
        return ThemeSwitchInteractorImpl(getThemeSwitchRepository(context))
    }
}