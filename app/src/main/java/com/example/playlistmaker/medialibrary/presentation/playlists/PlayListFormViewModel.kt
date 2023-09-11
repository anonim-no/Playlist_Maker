package com.example.playlistmaker.medialibrary.presentation.playlists

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayListFormViewModel(private val playListsInteractor: PlayListsInteractor) : ViewModel() {

    private var isClickAllowed = true

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