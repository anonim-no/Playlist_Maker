package com.example.playlistmaker.medialibrary.di

import com.example.playlistmaker.medialibrary.domain.db.favorites.FavoritesInteractor
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import com.example.playlistmaker.medialibrary.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.medialibrary.domain.impl.PlayListsInteractorImpl
import org.koin.dsl.module

val mediaLibraryInteractorModule = module {
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
    single<PlayListsInteractor> {
        PlayListsInteractorImpl(get())
    }
}