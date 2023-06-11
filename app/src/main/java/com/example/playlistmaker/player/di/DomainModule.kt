package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import org.koin.dsl.module

val playerDomainModule = module {
    factory<PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }
    factory<PlayerRepository> {
        PlayerRepositoryImpl(playerClient = get())
    }
}