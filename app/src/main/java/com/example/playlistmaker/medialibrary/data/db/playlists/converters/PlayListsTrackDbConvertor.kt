package com.example.playlistmaker.medialibrary.data.db.playlists.converters

import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.TrackEntity
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import java.util.Calendar

class PlayListsTrackDbConvertor {

    fun map(playList: PlayList): PlayListEntity {
        return PlayListEntity(
            null,
            playList.name,
            playList.description,
            playList.image,
            0,
            Calendar.getInstance().timeInMillis
        )
    }

    fun map(playListEntity: PlayListEntity): PlayList {
        return PlayList(
            playListEntity.playListId!!,
            playListEntity.name,
            playListEntity.description,
            playListEntity.image,
            playListEntity.tracksCount,
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl
        )
    }

    fun map(track: Track, playListId: Int): TrackEntity {
        return TrackEntity(
            null,
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
            playListId
        )
    }


}