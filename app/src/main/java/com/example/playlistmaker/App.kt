package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.di.playerDataModule
import com.example.playlistmaker.player.di.playerDomainModule
import com.example.playlistmaker.player.di.playerViewModelModule
import com.example.playlistmaker.settings.domain.api.ThemeSwitchInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PLAYLIST_MAKER_PREFERENCE = "playlist_maker_preference"
const val TRACK = "track"

class App : Application() {

    lateinit var themeSwitcherInteractor: ThemeSwitchInteractor

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@App)

            modules(
                playerDataModule,
                playerDomainModule,
                playerViewModelModule
            )

        }

        themeSwitcherInteractor = Creator.provideThemeSwitchInteractor(this)
        themeSwitcherInteractor.applyCurrentTheme()
    }

}