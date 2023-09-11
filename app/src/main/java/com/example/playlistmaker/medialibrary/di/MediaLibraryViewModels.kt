package com.example.playlistmaker.medialibrary.di

import com.example.playlistmaker.medialibrary.presentation.favorites.FavoritesTracksViewModel
import com.example.playlistmaker.medialibrary.presentation.playlists.PlayListFormViewModel
import com.example.playlistmaker.medialibrary.presentation.playlists.PlayListBottomSheetViewModel
import com.example.playlistmaker.medialibrary.presentation.playlists.PlayListViewModel
import com.example.playlistmaker.medialibrary.presentation.playlists.PlayListsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryViewModelsModule = module {
    viewModel {
        FavoritesTracksViewModel(favoritesInteractor = get())
    }
    viewModel {
        PlayListsViewModel(playListsInteractor = get())
    }
    viewModel {
        PlayListViewModel(playListsInteractor = get())
    }
    viewModel {
        PlayListFormViewModel(playListsInteractor = get())
    }
    viewModel {
        PlayListBottomSheetViewModel(
            playListsInteractor = get()
        )
    }
}