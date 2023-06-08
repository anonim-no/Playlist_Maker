package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

// интерфейс, с помощью которого слой Presentation будет общаться со слоем Domain
// интерфейс SearchInteractor реализует TracksInteractorImpl в Domain/Impl
interface SearchInteractor {

    fun searchTracks(expression: String, consumer: SearchConsumer)
    // callback для передачи результатов поискового запроса
    interface SearchConsumer {
        fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?)
    }

    fun addTracksHistory(track: Track)
    fun clearTracksHistory()
    fun getTracksHistory(): ArrayList<Track>

}