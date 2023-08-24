package com.example.playlistmaker.medialibrary.domain.db.favorites

import com.example.playlistmaker.common.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun getFavoritesTracks(): Flow<List<Track>>

    fun isFavoriteTrack(trackId: Int): Flow<Boolean>

    suspend fun addToFavorites(track: Track)

    suspend fun deleteFromFavorites(trackId: Int)
}