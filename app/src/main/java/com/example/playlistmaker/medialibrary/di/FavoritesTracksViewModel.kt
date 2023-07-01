package com.example.playlistmaker.medialibrary.di

import com.example.playlistmaker.medialibrary.ui.FavoritesTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoritesTracksViewModelModule = module {
    viewModel {
        FavoritesTracksViewModel()
    }
}