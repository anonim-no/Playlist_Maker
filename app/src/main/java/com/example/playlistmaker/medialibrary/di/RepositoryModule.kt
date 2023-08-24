package com.example.playlistmaker.medialibrary.di

import com.example.playlistmaker.medialibrary.data.FavoritesRepositoryImpl
import com.example.playlistmaker.medialibrary.data.db.favorites.converters.FavoritesTrackDbConvertor
import com.example.playlistmaker.medialibrary.domain.db.favorites.FavoritesRepository
import org.koin.dsl.module

val mediaLibraryRepositoryModule = module {
    factory { FavoritesTrackDbConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}