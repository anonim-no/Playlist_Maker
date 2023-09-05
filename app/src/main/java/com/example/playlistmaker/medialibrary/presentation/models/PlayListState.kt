package com.example.playlistmaker.medialibrary.presentation.models

import com.example.playlistmaker.common.models.Track

sealed interface PlayListState {

    data class PlayList(
        val tracks: List<Track>
    ) : PlayListState

}