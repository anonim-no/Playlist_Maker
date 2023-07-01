package com.example.playlistmaker.medialibrary.di

import com.example.playlistmaker.medialibrary.ui.PlayListsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playListsViewModelModule = module {
    viewModel {
        PlayListsViewModel()
    }
}