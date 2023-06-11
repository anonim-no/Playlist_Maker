package com.example.playlistmaker.search.di

import android.content.Context
import com.example.playlistmaker.APPLE_MUSIC_API_BASE_URL
import com.example.playlistmaker.PLAYLIST_MAKER_PREFERENCE
import com.example.playlistmaker.search.data.LocalStorage
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.local.SharedPreferencesClient
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchDataModule = module {

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl(APPLE_MUSIC_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(PLAYLIST_MAKER_PREFERENCE, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<LocalStorage> {
        SharedPreferencesClient(
            sharedPreferences = get(),
            gson = get()
        )
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            iTunesApiService = get(),
            context = androidContext()
        )
    }

}