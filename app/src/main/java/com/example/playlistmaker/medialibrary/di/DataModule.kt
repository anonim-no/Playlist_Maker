package com.example.playlistmaker.medialibrary.di

import androidx.room.Room
import com.example.playlistmaker.medialibrary.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaLibraryDataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}