package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchRepository
import org.koin.dsl.module

val searchRepositoryModule = module {
    single<SearchRepository> {
        SearchRepositoryImpl(networkClient = get(), localStorage = get())
    }
}