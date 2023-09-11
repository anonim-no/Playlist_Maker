package com.example.playlistmaker.medialibrary.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.models.PlayList
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayListBottomSheetViewModel(
    private val playListsInteractor: PlayListsInteractor
): ViewModel() {

    private var isClickAllowed = true

    fun deletePlaylist(playList: PlayList, onResultListener: () -> Unit) {
        viewModelScope.launch {
            playListsInteractor.deletePlaylist(playList)
            onResultListener()
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