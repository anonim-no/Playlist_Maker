package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AndroidMediaPlayer
import com.example.playlistmaker.player.data.PlayerClient
import org.koin.dsl.module

val playerDataModule = module {
    factory<PlayerClient> {
        AndroidMediaPlayer(mediaPlayer = get())
    }
    factory {
        MediaPlayer()
    }
}