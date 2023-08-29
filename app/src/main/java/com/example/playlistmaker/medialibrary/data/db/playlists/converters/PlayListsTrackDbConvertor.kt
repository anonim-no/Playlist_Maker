package com.example.playlistmaker.medialibrary.data.db.playlists.converters

import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListWithCountTracks
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListsTrackEntity
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import java.util.Calendar

class PlayListsTrackDbConvertor {

    fun map(playList: PlayList): PlayListEntity {
        return PlayListEntity(
            null,
            playList.name,
            playList.description,
            playList.image,
            Calendar.getInstance().timeInMillis
        )
    }

    fun map(playListWithCountTracks: PlayListWithCountTracks): PlayList {
        return PlayList(
            playListWithCountTracks.playListId!!,
            playListWithCountTracks.name,
            playListWithCountTracks.description,
            playListWithCountTracks.image,
            playListWithCountTracks.tracksCount,
        )
    }

    fun map(playListsTrackEntity: PlayListsTrackEntity): Track {
        playListsTrackEntity.apply {
            return Track(
                trackId,
                trackName,
                artistName,
                trackTimeMillis,
                artworkUrl100,
                collectionName,
                releaseDate,
                primaryGenreName,
                country,
                previewUrl
            )
        }
    }

    fun map(track: Track): PlayListsTrackEntity {
        track.apply {
            return PlayListsTrackEntity(
                trackId,
                trackName,
                artistName,
                trackTimeMillis,
                artworkUrl100,
                collectionName,
                releaseDate,
                primaryGenreName,
                country,
                previewUrl,
            )
        }
    }


}