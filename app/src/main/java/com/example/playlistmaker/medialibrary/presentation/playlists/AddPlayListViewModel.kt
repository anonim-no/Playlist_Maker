package com.example.playlistmaker.medialibrary.presentation.playlists

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import kotlinx.coroutines.launch

class AddPlayListViewModel(private val playListsInteractor: PlayListsInteractor) : ViewModel() {

    fun createPlayList(name: String, description: String, pickImageUri: Uri?) {
        viewModelScope.launch {
            playListsInteractor.addPlayList(name, description, pickImageUri)
        }
    }



}