package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(private val playerClient: PlayerClient) : PlayerRepository {

    override fun preparePlayer(
        url: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        playerClient.preparePlayer(
            url,
            onPreparedListener,
            onCompletionListener
        )
    }

    override fun startPlayer() {
        playerClient.startPlayer()
    }

    override fun pausePlayer() {
        playerClient.pausePlayer()
    }

    override fun isPlaying(): Boolean {
        return playerClient.isPlaying()
    }

    override fun getCurrentPosition(): Int {
        return playerClient.getCurrentPosition()
    }

}