package com.example.playlistmaker.medialibrary.presentation.models

import com.example.playlistmaker.medialibrary.domain.models.PlayList

sealed interface PlayListsState {

    object Empty : PlayListsState

    data class PlayLists(
        val playLists: List<PlayList>
    ) : PlayListsState

}