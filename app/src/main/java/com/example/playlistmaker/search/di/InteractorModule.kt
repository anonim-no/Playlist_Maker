package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import org.koin.dsl.module

val searchInteractorModule = module {
    single<SearchInteractor> {
        SearchInteractorImpl(repository = get())
    }
}