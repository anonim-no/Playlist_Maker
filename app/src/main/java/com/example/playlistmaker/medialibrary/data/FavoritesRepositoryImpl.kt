package com.example.playlistmaker.medialibrary.data

import com.example.playlistmaker.medialibrary.data.db.AppDatabase
import com.example.playlistmaker.medialibrary.data.db.converters.TrackDbConvertor
import com.example.playlistmaker.medialibrary.data.db.entity.TrackEntity
import com.example.playlistmaker.medialibrary.domain.db.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {
    override fun getFavoritesTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.movieDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun isFavoriteTrack(trackId: Int): Flow<Boolean> = flow {
        val isFavorite = appDatabase.movieDao().isFavoriteTrack(trackId)
        emit(isFavorite)
    }

    override suspend fun addToFavorites(track: Track) {
        appDatabase.movieDao().addToFavorites(trackDbConvertor.map(track))
    }

    override suspend fun deleteFromFavorites(trackId: Int) {
        appDatabase.movieDao().deleteFromFavorites(trackId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}