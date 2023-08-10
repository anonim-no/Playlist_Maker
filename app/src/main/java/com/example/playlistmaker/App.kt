package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.medialibrary.di.mediaLibraryDataModule
import com.example.playlistmaker.medialibrary.di.mediaLibraryInteractorModule
import com.example.playlistmaker.medialibrary.di.mediaLibraryRepositoryModule
import com.example.playlistmaker.medialibrary.di.mediaLibraryViewModelsModule
import com.example.playlistmaker.player.di.playerDataModule
import com.example.playlistmaker.player.di.playerInteractorModule
import com.example.playlistmaker.player.di.playerRepositoryModule
import com.example.playlistmaker.player.di.playerViewModelModule
import com.example.playlistmaker.search.di.searchDataModule
import com.example.playlistmaker.search.di.searchInteractorModule
import com.example.playlistmaker.search.di.searchRepositoryModule
import com.example.playlistmaker.search.di.searchViewModelModule
import com.example.playlistmaker.settings.di.settingsDataModule
import com.example.playlistmaker.settings.di.settingsInteractorModule
import com.example.playlistmaker.settings.di.settingsRepositoryModule
import com.example.playlistmaker.settings.di.settingsViewModelModule
import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor
import org.koin.android.ext.android.inject
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
                mediaLibraryViewModelsModule,
                mediaLibraryDataModule,
                mediaLibraryInteractorModule,
                mediaLibraryRepositoryModule,
            )

        }

        val themeSwitcherInteractor: ThemeSwitchInteractor by inject()
        themeSwitcherInteractor.applyCurrentTheme()
    }

}