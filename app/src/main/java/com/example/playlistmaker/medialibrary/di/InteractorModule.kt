package com.example.playlistmaker.medialibrary.di

import com.example.playlistmaker.medialibrary.domain.db.favorites.FavoritesInteractor
import com.example.playlistmaker.medialibrary.domain.impl.FavoritesInteractorImpl
import org.koin.dsl.module

val mediaLibraryInteractorModule = module {
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}