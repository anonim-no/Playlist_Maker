package com.example.playlistmaker.medialibrary.presentation.models

import com.example.playlistmaker.common.models.Track

sealed interface FavoritesTracksState {

    object Empty : FavoritesTracksState

    data class FavoritesTracks(
        val tracks: List<Track>
    ) : FavoritesTracksState

}