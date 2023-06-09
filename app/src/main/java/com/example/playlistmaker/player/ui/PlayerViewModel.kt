package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.ui.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel : ViewModel() {

    private val playerInteractor = Creator.providePlayerInteractor()

    private val stateLiveData = MutableLiveData<PlayerState>()

    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val updatePlayingTimeRunnable = Runnable { updatePlayingTime() }

    fun preparePlayer(url: String) {
        renderState(PlayerState.Preparing)
        playerInteractor.preparePlayer(
            url = url,
            onPreparedListener = {
                renderState(PlayerState.Stopped)
            },
            onCompletionListener = {
                handler.removeCallbacks(updatePlayingTimeRunnable)
                renderState(PlayerState.Stopped)
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        renderState(PlayerState.Playing)
        handler.postDelayed(updatePlayingTimeRunnable, UPDATE_PLAYING_TIME_DELAY)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        renderState(PlayerState.Paused)
        handler.removeCallbacks(updatePlayingTimeRunnable)
    }

    private fun isPlaying(): Boolean {
        return playerInteractor.isPlaying()
    }

    private fun getCurrentPosition(): Int {
        return playerInteractor.getCurrentPosition()
    }

    fun playbackControl() {
        if (isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    private fun updatePlayingTime() {
        renderState(
            PlayerState.UpdatePlayingTime(
                SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(
                    getCurrentPosition()
                )
            )
        )
        handler.postDelayed(updatePlayingTimeRunnable, UPDATE_PLAYING_TIME_DELAY)
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val UPDATE_PLAYING_TIME_DELAY = 500L
    }
}