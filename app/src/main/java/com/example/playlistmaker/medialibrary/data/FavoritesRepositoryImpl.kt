package com.example.playlistmaker.medialibrary.data

import com.example.playlistmaker.medialibrary.data.db.AppDatabase
import com.example.playlistmaker.medialibrary.data.db.favorites.converters.FavoritesTrackDbConvertor
import com.example.playlistmaker.medialibrary.data.db.favorites.entity.FavoritesTrackEntity
import com.example.playlistmaker.medialibrary.domain.db.favorites.FavoritesRepository
import com.example.playlistmaker.common.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favoritesTrackDbConvertor: FavoritesTrackDbConvertor
) : FavoritesRepository {
    override fun getFavoritesTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoritesTrackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun isFavoriteTrack(trackId: Int): Flow<Boolean> = flow {
        val isFavorite = appDatabase.favoritesTrackDao().isFavoriteTrack(trackId)
        emit(isFavorite)
    }

    override suspend fun addToFavorites(track: Track) =
        appDatabase.favoritesTrackDao().addToFavorites(favoritesTrackDbConvertor.map(track))

    override suspend fun deleteFromFavorites(trackId: Int) =
        appDatabase.favoritesTrackDao().deleteFromFavorites(trackId)

    private fun convertFromTrackEntity(tracks: List<FavoritesTrackEntity>): List<Track> =
        tracks.map { track -> favoritesTrackDbConvertor.map(track) }

}