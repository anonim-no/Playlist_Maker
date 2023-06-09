package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.SearchResponse
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.Resource

// Класс SearchRepositoryImpl - реализация интерфейса SearchRepository
// Задача этой реализации — сделать запрос и получить ответ от сервера, используя сетевой клиент
class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: LocalStorage
    ) : SearchRepository {

    override fun searchTracks(expression: String): Resource<ArrayList<Track>> {
        val response = networkClient.doRequest(SearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                return Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                // ArrayList<TrackDto> -> ArrayList<Track>
                val arrayListTracks = arrayListOf<Track>()
                (response as SearchResponse).results.forEach {
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

    override fun addTracksHistory(track: Track) {
        localStorage.addTracksHistory(track)
    }

    override fun clearTracksHistory() {
        localStorage.clearTracksHistory()
    }

    override fun getTracksHistory(): ArrayList<Track> {
        return localStorage.getTracksHistory()
    }
}