package com.example.playlistmaker.medialibrary.presentation.models

import com.example.playlistmaker.common.models.PlayList
import com.example.playlistmaker.common.models.Track

sealed interface PlayListState {

    data class PlayListInfo(
        val playList: PlayList
    ) : PlayListState

    data class PlayListTracks(
        val tracks: List<Track>
    ) : PlayListState

}