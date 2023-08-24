package com.example.playlistmaker.medialibrary.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import kotlinx.coroutines.launch

class AddPlayListViewModel(private val playListsInteractor: PlayListsInteractor) : ViewModel() {

    fun createPlayList(name: String, description: String, image: String?) {
        val playList = PlayList(
            playListId = 0,
            name = name,
            description = description,
            image = image,
            tracksCount = 0
        )
        viewModelScope.launch {
            playListsInteractor.addPlayList(playList)
        }
    }



}