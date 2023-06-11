package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import org.koin.dsl.module

val playerRepositoryModule = module {
    factory<PlayerRepository> {
        PlayerRepositoryImpl(playerClient = get())
    }
}