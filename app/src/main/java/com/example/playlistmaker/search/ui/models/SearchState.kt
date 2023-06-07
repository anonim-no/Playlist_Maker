package com.example.playlistmaker.search.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    object Loading : SearchState

    object NotFound : SearchState

    data class SearchResult(
        val tracks: ArrayList<Track>
    ) : SearchState

    data class History(
        val tracks: ArrayList<Track>
    ) : SearchState

    data class Error(
        val message: String
    ) : SearchState

}
