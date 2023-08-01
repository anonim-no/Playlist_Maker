package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

// интерфейс, с помощью которого слой Presentation будет общаться со слоем Domain
// интерфейс SearchInteractor реализует TracksInteractorImpl в Domain/Impl
interface SearchInteractor {

    fun searchTracks(expression: String): Flow<Pair<ArrayList<Track>?, Int?>>

    fun addTracksHistory(track: Track)
    fun clearTracksHistory()
    fun getTracksHistory(): ArrayList<Track>

}