package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource

// интерфейс для связи слоя Domain со слоем Data
// интерфейс TracksRepository реализует TracksRepositoryImpl в data
interface TracksRepository {
    fun searchTracks(expression: String): Resource<ArrayList<Track>>
}