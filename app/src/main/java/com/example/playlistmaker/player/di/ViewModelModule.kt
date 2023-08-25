package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerViewModelModule = module {
    viewModel {
        PlayerViewModel(playerInteractor = get(), favoritesInteractor = get(), playListsInteractor = get())
    }
}