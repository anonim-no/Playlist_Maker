package com.example.playlistmaker.medialibrary.data.db.favorites.converters

import com.example.playlistmaker.medialibrary.data.db.favorites.entity.FavoritesTrackEntity
import com.example.playlistmaker.common.models.Track
import java.util.Calendar

class FavoritesTrackDbConvertor {
    fun map(track: Track): FavoritesTrackEntity {
        track.apply {
            return FavoritesTrackEntity(
                trackId,
                trackName,
                artistName,
                trackTimeMillis,
                artworkUrl60,
                collectionName,
                releaseDate,
                primaryGenreName,
                country,
                previewUrl,
                Calendar.getInstance().timeInMillis
            )
        }
    }

    fun map(track: FavoritesTrackEntity): Track {
        track.apply {
            return Track(
                trackId,
                trackName,
                artistName,
                trackTimeMillis,
                artworkUrl60,
                collectionName,
                releaseDate,
                primaryGenreName,
                country,
                previewUrl
            )
        }
    }
}