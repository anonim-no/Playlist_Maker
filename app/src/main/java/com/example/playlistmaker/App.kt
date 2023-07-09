package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.medialibrary.di.mediaLibraryViewModelsModule
import org.koin.android.ext.android.inject
import com.example.playlistmaker.player.di.*
import com.example.playlistmaker.search.di.*
import com.example.playlistmaker.settings.di.*
import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PLAYLIST_MAKER_PREFERENCE = "playlist_maker_preference"
const val TRACK = "track"
const val APPLE_MUSIC_API_BASE_URL = "http://itunes.apple.com"

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@App)

            modules(
                playerDataModule,
                playerRepositoryModule,
                playerInteractorModule,
                playerViewModelModule,
                searchDataModule,
                searchRepositoryModule,
                searchInteractorModule,
                searchViewModelModule,
                settingsDataModule,
                settingsRepositoryModule,
                settingsInteractorModule,
                settingsViewModelModule,
                mediaLibraryViewModelsModule
            )

        }

        val themeSwitcherInteractor: ThemeSwitchInteractor by inject()
        themeSwitcherInteractor.applyCurrentTheme()
    }

}