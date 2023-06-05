package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource

// Класс TracksRepositoryImpl - реализация интерфейса TracksRepository
// Задача этой реализации — сделать запрос и получить ответ от сервера, используя сетевой клиент
class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Resource<ArrayList<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                return Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                // ArrayList<TrackDto> -> ArrayList<Track>
                val arrayListTracks = arrayListOf<Track>()
                (response as TracksSearchResponse).results.forEach {
                    arrayListTracks.add(
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                        )
                    )
                }
                return Resource.Success(arrayListTracks)
            }
            else -> {
                return Resource.Error("Ошибка сервера")
            }
        }

    }
}