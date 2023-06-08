package com.example.playlistmaker.player.ui.models

sealed interface PlayerState {

    object Preparing : PlayerState

    object Stopped : PlayerState

    object Playing : PlayerState

    object Paused : PlayerState

    data class UpdatePlayingTime(
        val playingTime: String
    ) : PlayerState

}