package com.example.playlistmaker.medialibrary.presentation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import com.example.playlistmaker.medialibrary.presentation.models.PlayListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayListViewModel(
    private val playListsInteractor: PlayListsInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayListState>()
    fun observeState(): LiveData<PlayListState> = stateLiveData

    private var isClickAllowed = true

    private fun renderState(state: PlayListState) {
        stateLiveData.postValue(state)
    }

    fun requestTracks(playListId: Int) {
        viewModelScope.launch {
            val tracks = playListsInteractor.getPlayListTracks(playListId)
            renderState(PlayListState.PlayList(tracks))
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