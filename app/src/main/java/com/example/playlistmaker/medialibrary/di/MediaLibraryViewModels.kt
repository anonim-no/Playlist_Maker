package com.example.playlistmaker.medialibrary.di

import com.example.playlistmaker.medialibrary.ui.favorites.FavoritesTracksViewModel
import com.example.playlistmaker.medialibrary.ui.playlists.PlayListsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryViewModelsModule = module {
    viewModel {
        FavoritesTracksViewModel(get())
    }
    viewModel {
        PlayListsViewModel()
    }
}