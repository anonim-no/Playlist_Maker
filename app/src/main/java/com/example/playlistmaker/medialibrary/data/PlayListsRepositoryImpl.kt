package com.example.playlistmaker.medialibrary.data

import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.data.db.AppDatabase
import com.example.playlistmaker.medialibrary.data.db.playlists.converters.PlayListsTrackDbConvertor
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListsTrackEntity
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsRepository
import com.example.playlistmaker.medialibrary.domain.models.PlayList

class PlayListsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playListsTrackDbConvertor: PlayListsTrackDbConvertor
) : PlayListsRepository {
    override suspend fun addPlayList(playList: PlayList) {
        appDatabase.playListsTrackDao().addPlayList(
            playListsTrackDbConvertor.map(playList)
        )
    }

    override suspend fun addTrackToPlayList(track: Track, playListId: Int) {
        appDatabase.playListsTrackDao().addTrackToPlayList(
            playListsTrackDbConvertor.map(track, playListId)
        )
    }

    override suspend fun getPlayLists(): List<PlayList> {
        val playLists = appDatabase.playListsTrackDao().getPlayLists()
        return convertPlayListEntityToPlayList(playLists)
    }

    override suspend fun getPlayListTracks(playListId: Int): List<Track> {
        val tracks = appDatabase.playListsTrackDao().getPlayListTracks(playListId)
        return convertPlayListsTrackEntityToTrack(tracks)
    }

    private fun convertPlayListsTrackEntityToTrack(tracks: List<PlayListsTrackEntity>): List<Track> {
        return tracks.map { playListsTrackDbConvertor.map(it) }
    }

    private fun convertPlayListEntityToPlayList(playListEntity: List<PlayListEntity>): List<PlayList> {
        return playListEntity.map { playListsTrackDbConvertor.map(it) }
    }

}