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

    private val playerStateLiveData = MutableLiveData<PlayerState>()

    private val trackTimeStateLiveData = MutableLiveData<PlayerState.UpdatePlayingTime>()

    fun observePlayerStateState(): LiveData<PlayerState> = playerStateLiveData

    fun observeTrackTimeState(): LiveData<PlayerState.UpdatePlayingTime> = trackTimeStateLiveData

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
                trackTimeStateLiveData.postValue(
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
        playerStateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
    }

    companion object {
        private const val UPDATE_PLAYING_TIME_DELAY = 300L
    }
}