package com.example.playlistmaker.medialibrary.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.models.PlayList
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import kotlinx.coroutines.launch

class PlayListBottomSheetViewModel(
    private val playListsInteractor: PlayListsInteractor
): ViewModel() {

    fun deletePlaylist(playList: PlayList) {
        viewModelScope.launch {
            playListsInteractor.deletePlaylist(playList)
        }
    }

}