package com.example.playlistmaker.medialibrary.presentation.models

import com.example.playlistmaker.common.models.PlayList

sealed interface PlayListsState {

    object Empty : PlayListsState

    data class PlayLists(
        val playLists: List<PlayList>
    ) : PlayListsState

}