package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.ui.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>()

    fun observeState(): LiveData<PlayerState> = stateLiveData

    private var timerJob: Job? = null

    fun preparePlayer(url: String?) {
        renderState(PlayerState.Preparing)
        if (url != null) {
            playerInteractor.preparePlayer(
                url = url,
                onPreparedListener = {
                    renderState(PlayerState.Stopped)
                },
                onCompletionListener = {
                    timerJob?.cancel()
                    renderState(PlayerState.Stopped)
                }
            )
        } else {
            renderState(PlayerState.Unplayable)
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        renderState(PlayerState.Playing)
        updatePlayingTime()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        renderState(PlayerState.Paused)
        timerJob?.cancel()
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
        timerJob = viewModelScope.launch {
            while (isPlaying()) {
                delay(UPDATE_PLAYING_TIME_DELAY)
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
            }
        }
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val UPDATE_PLAYING_TIME_DELAY = 300L
    }
}