package com.example.playlistmaker.medialibrary.di

import com.example.playlistmaker.medialibrary.data.FavoritesRepositoryImpl
import com.example.playlistmaker.medialibrary.data.db.converters.TrackDbConvertor
import com.example.playlistmaker.medialibrary.domain.db.FavoritesRepository
import org.koin.dsl.module

val mediaLibraryRepositoryModule = module {
    factory { TrackDbConvertor() }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}