package com.example.playlistmaker.medialibrary.domain.impl

import com.example.playlistmaker.medialibrary.domain.db.favorites.FavoritesInteractor
import com.example.playlistmaker.medialibrary.domain.db.favorites.FavoritesRepository
import com.example.playlistmaker.common.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) :
    FavoritesInteractor {
    override fun getFavoritesTracks(): Flow<List<Track>> = favoritesRepository.getFavoritesTracks()

    override fun isFavoriteTrack(trackId: Int): Flow<Boolean> =
        favoritesRepository.isFavoriteTrack(trackId)

    override suspend fun addToFavorites(track: Track) = favoritesRepository.addToFavorites(track)

    override suspend fun deleteFromFavorites(trackId: Int) =
        favoritesRepository.deleteFromFavorites(trackId)
}