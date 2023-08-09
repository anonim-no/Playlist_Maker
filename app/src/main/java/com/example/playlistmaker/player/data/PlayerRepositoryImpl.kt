package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(private val playerClient: PlayerClient) : PlayerRepository {

    override fun preparePlayer(
        url: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        playerClient.preparePlayer(
            url = url,
            onPreparedListener = onPreparedListener,
            onCompletionListener = onCompletionListener
        )
    }

    override fun startPlayer() {
        playerClient.startPlayer()
    }

    override fun pausePlayer() {
        playerClient.pausePlayer()
    }

    override fun isPlaying(): Boolean = playerClient.isPlaying()

    override fun getCurrentPosition(): Int = playerClient.getCurrentPosition()

    override fun releasePlayer() {
        playerClient.releasePlayer()
    }

}