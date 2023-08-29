package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import com.example.playlistmaker.player.presentation.models.PlayListsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerBottomSheetViewModel(
    private val playListsInteractor: PlayListsInteractor
): ViewModel() {
    private val playListsStateLiveData = MutableLiveData<PlayListsState>()

    fun observePlayListsState(): LiveData<PlayListsState> = playListsStateLiveData

    private var isClickAllowed = true

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
            if (playListsInteractor.isTrackInPlayList(track.trackId, playList.playListId)) {
                playListsStateLiveData.postValue(
                    PlayListsState.AddTrackToPlayListResult(false, playListName = playList.name)
                )
            } else {
                playListsInteractor.addTrackToPlayList(track, playList.playListId)
                playListsStateLiveData.postValue(
                    PlayListsState.AddTrackToPlayListResult(true, playListName = playList.name)
                )
            }
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}