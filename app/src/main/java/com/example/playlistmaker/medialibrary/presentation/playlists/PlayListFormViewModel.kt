package com.example.playlistmaker.medialibrary.presentation.playlists

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import kotlinx.coroutines.launch

class PlayListFormViewModel(private val playListsInteractor: PlayListsInteractor) : ViewModel() {

    fun createPlayList(
        name: String,
        description: String,
        pickImageUri: Uri?,
        onResultListener: () -> Unit
    ) {
        viewModelScope.launch {
            playListsInteractor.addPlayList(name, description, pickImageUri)
            onResultListener()
        }
    }

    fun editPlayList(
        playListId: Int,
        name: String,
        description: String,
        pickImageUri: Uri?,
        onResultListener: () -> Unit
    ) {
        viewModelScope.launch {
            playListsInteractor.editPlayList(playListId, name, description, pickImageUri)
            onResultListener()
        }
    }


}