package com.example.playlistmaker.search.presentation.models

import com.example.playlistmaker.common.models.Track

sealed interface SearchState {

    object Loading : SearchState

    object NotFound : SearchState

    data class SearchResult(
        val tracks: List<Track>
    ) : SearchState

    data class History(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val errorCode: Int
    ) : SearchState

}
