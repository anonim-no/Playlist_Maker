package com.example.playlistmaker.common.di

import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.dsl.module

val analyticsModule = module {
    single {
        FirebaseAnalytics.getInstance(get())
    }
}