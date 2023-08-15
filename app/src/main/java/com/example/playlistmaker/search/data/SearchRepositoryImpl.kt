package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.SearchResponse
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Класс SearchRepositoryImpl - реализация интерфейса SearchRepository
// Задача этой реализации — сделать запрос и получить ответ от сервера, используя сетевой клиент
class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: LocalStorage
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {

        val response = networkClient.doRequest(SearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(response.resultCode)) // "Проверьте подключение к интернету"
            }

            200 -> {
                val tracks: List<Track> = (response as SearchResponse).results.map {
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
                }
                emit(Resource.Success(tracks))
            }

            else -> {
                emit(Resource.Error(response.resultCode)) // другая ошибка
            }
        }

    }

    override fun addTracksHistory(track: Track) {
        localStorage.addTracksHistory(track)
    }

    override fun clearTracksHistory() {
        localStorage.clearTracksHistory()
    }

    override fun getTracksHistory(): List<Track> {
        return localStorage.getTracksHistory()
    }
}