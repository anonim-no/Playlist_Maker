package com.example.playlistmaker.medialibrary.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.medialibrary.ui.models.PlayListsState

class PlayListsViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<PlayListsState>()
    fun observeState(): LiveData<PlayListsState> = stateLiveData

    // (пока ничего не показываем)
    init {
        renderState(PlayListsState.Empty)
    }

    private fun renderState(state: PlayListsState) {
        stateLiveData.postValue(state)
    }
}