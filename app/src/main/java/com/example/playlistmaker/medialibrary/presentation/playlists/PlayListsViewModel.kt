package com.example.playlistmaker.medialibrary.presentation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import com.example.playlistmaker.medialibrary.presentation.models.PlayListsState
import kotlinx.coroutines.launch

class PlayListsViewModel(private val playListsInteractor: PlayListsInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlayListsState>()
    fun observeState(): LiveData<PlayListsState> = stateLiveData

    private fun renderState(state: PlayListsState) {
        stateLiveData.postValue(state)
    }

    fun clickDebounce(): Boolean {
        return true
    }

    fun getPlayLists() {
        viewModelScope.launch {
            val playLists = playListsInteractor.getPlayLists()
            if (playLists.isEmpty()) {
                renderState(PlayListsState.Empty)
            } else {
                renderState(PlayListsState.PlayLists(playLists))
            }
        }
    }
}