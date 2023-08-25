package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.db.favorites.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.presentation.models.PlayerState
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import com.example.playlistmaker.player.presentation.models.PlayListsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playListsInteractor: PlayListsInteractor
) : ViewModel() {

    private val playerStateLiveData = MutableLiveData<PlayerState>()

    private val trackTimeStateLiveData = MutableLiveData<PlayerState.UpdatePlayingTime>()

    private val trackFavoriteStateLiveData = MutableLiveData<PlayerState.StateFavorite>()

    private val playListsStateLiveData = MutableLiveData<PlayListsState>()

    fun observePlayerStateState(): LiveData<PlayerState> = playerStateLiveData

    fun observeTrackTimeState(): LiveData<PlayerState.UpdatePlayingTime> = trackTimeStateLiveData

    fun observeFavoriteState(): LiveData<PlayerState.StateFavorite> = trackFavoriteStateLiveData

    fun observePlayListsState(): LiveData<PlayListsState> = playListsStateLiveData

    private var timerJob: Job? = null

    private var isTrackFavorite = false

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
        if (isPlaying()) {
            playerInteractor.pausePlayer()
            renderState(PlayerState.Paused)
            timerJob?.cancel()
        }
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

    fun isFavorite(trackId: Int) {
        viewModelScope.launch {
            favoritesInteractor
                .isFavoriteTrack(trackId)
                .collect { isFavorite ->
                    isTrackFavorite = isFavorite
                    trackFavoriteStateLiveData.postValue(
                        PlayerState.StateFavorite(isFavorite)
                    )
                }
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            isTrackFavorite = if (isTrackFavorite) {
                favoritesInteractor.deleteFromFavorites(track.trackId)
                trackFavoriteStateLiveData.postValue(
                    PlayerState.StateFavorite(false)
                )
                false
            } else {
                favoritesInteractor.addToFavorites(track)
                trackFavoriteStateLiveData.postValue(
                    PlayerState.StateFavorite(true)
                )
                true
            }
        }
    }

    fun getPlayLists() {
        viewModelScope.launch {
            val playLists = playListsInteractor.getPlayLists()
            if (playLists.isEmpty()) {
                playListsStateLiveData.postValue(PlayListsState.Empty)
            } else {
                playListsStateLiveData.postValue(PlayListsState.PlayLists(playLists))
            }
        }
    }

    fun addTrackToPlayList(track: Track, playList: PlayList) {
        viewModelScope.launch {
            playListsInteractor.addTrackToPlayList(track, playList.playListId)
        }
    }

    companion object {
        private const val UPDATE_PLAYING_TIME_DELAY = 300L
    }
}