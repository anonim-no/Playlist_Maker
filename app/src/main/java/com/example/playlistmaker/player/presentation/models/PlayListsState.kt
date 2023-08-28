package com.example.playlistmaker.player.presentation.models

import com.example.playlistmaker.medialibrary.domain.models.PlayList

sealed interface PlayListsState {

    object Empty : PlayListsState

    data class AddTrackToPlayListResult(
        val isAdded: Boolean,
        val playListName: String
    ) : PlayListsState

    data class PlayLists(
        val playLists: List<PlayList>
    ) : PlayListsState

}