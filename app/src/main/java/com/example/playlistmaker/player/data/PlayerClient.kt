package com.example.playlistmaker.player.data

interface PlayerClient {
    fun preparePlayer(url: String, onPreparedListener: () -> Unit, onCompletionListener: () -> Unit)

    fun startPlayer()

    fun pausePlayer()

    fun isPlaying(): Boolean

    fun getCurrentPosition(): Int
}