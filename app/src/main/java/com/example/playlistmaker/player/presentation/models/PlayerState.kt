package com.example.playlistmaker.player.presentation.models

sealed interface PlayerState {

    object Preparing : PlayerState

    object Stopped : PlayerState

    object Unplayable : PlayerState

    object Playing : PlayerState

    object Paused : PlayerState

    data class UpdatePlayingTime(
        val playingTime: String
    ) : PlayerState

    data class StateFavorite(
        val isFavorite: Boolean
    ) : PlayerState

}