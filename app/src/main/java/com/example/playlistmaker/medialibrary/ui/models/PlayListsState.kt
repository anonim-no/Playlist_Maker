package com.example.playlistmaker.medialibrary.ui.models

import com.example.playlistmaker.medialibrary.domain.models.PlayList

sealed interface PlayListsState {

    object Empty : PlayListsState

    data class PlayLists(
        val tracks: ArrayList<PlayList>
    ) : PlayListsState

}