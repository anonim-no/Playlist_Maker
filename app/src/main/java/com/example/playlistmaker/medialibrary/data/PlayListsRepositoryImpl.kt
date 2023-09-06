package com.example.playlistmaker.medialibrary.data

import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.data.db.AppDatabase
import com.example.playlistmaker.medialibrary.data.db.playlists.converters.PlayListsTrackDbConvertor
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListWithCountTracks
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListsTrackEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.TrackPlayListEntity
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsRepository
import com.example.playlistmaker.common.models.PlayList

class PlayListsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playListsTrackDbConvertor: PlayListsTrackDbConvertor
) : PlayListsRepository {

    override suspend fun addPlayList(playList: PlayList) =
        appDatabase.playListsTrackDao().addPlayList(
            playListsTrackDbConvertor.map(playList)
        )

    override suspend fun addTrackToPlayList(track: Track, playListId: Int) =
        appDatabase.playListsTrackDao().addTrackToPlayList(
            playListsTrackEntity = playListsTrackDbConvertor.map(track),
            trackPlayListEntity = TrackPlayListEntity(null, playListId, track.trackId)
        )

    override suspend fun getPlayLists(): List<PlayList> =
        convertPlayListWithCountTracksToPlayList(
            appDatabase.playListsTrackDao().getPlayLists()
        )

    override suspend fun getPlayListTracks(playListId: Int): List<Track> =
        convertPlayListsTrackEntityToTrack(
            appDatabase.playListsTrackDao().getPlayListTracks(playListId)
        )

    override suspend fun isTrackInPlayList(trackId: Int, playListId: Int): Boolean =
        appDatabase.playListsTrackDao().isTrackInPlayList(trackId, playListId)

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playListId: Int) {
        appDatabase.playListsTrackDao().deleteTrackFromPlayList(trackId, playListId)
    }


    private fun convertPlayListsTrackEntityToTrack(tracks: List<PlayListsTrackEntity>): List<Track> =
        tracks.map {
            playListsTrackDbConvertor.map(it)
        }

    private fun convertPlayListWithCountTracksToPlayList(playListWithCountTracks: List<PlayListWithCountTracks>): List<PlayList> =
        playListWithCountTracks.map {
            playListsTrackDbConvertor.map(it)
        }

}