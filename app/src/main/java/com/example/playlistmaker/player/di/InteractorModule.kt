package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import org.koin.dsl.module

val playerInteractorModule = module {
    factory<PlayerInteractor> {
        PlayerInteractorImpl(repository = get())
    }
}