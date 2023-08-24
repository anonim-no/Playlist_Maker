package com.example.playlistmaker.medialibrary.data.db.favorites.converters

import com.example.playlistmaker.medialibrary.data.db.favorites.entity.FavoritesTrackEntity
import com.example.playlistmaker.common.models.Track
import java.util.Calendar

class FavoritesTrackDbConvertor {
    fun map(track: Track): FavoritesTrackEntity {
        return FavoritesTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            Calendar.getInstance().timeInMillis
        )
    }

    fun map(track: FavoritesTrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}