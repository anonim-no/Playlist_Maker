package com.example.playlistmaker.medialibrary.data

import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.data.db.AppDatabase
import com.example.playlistmaker.medialibrary.data.db.favorites.converters.FavoritesTrackDbConvertor
import com.example.playlistmaker.medialibrary.data.db.playlists.converters.PlayListsTrackDbConvertor
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsRepository
import com.example.playlistmaker.medialibrary.domain.models.PlayList

class PlayListsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playListsTrackDbConvertor: PlayListsTrackDbConvertor
): PlayListsRepository {
    override suspend fun addPlayList(playList: PlayList) {
        appDatabase.playListsTrackDao().addPlayList(playListsTrackDbConvertor.map(playList))
    }

    override suspend fun addTrackToPlayList(track: Track, playListId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayLists(): List<PlayList> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayListTracks(playListId: Int): List<Track> {
        TODO("Not yet implemented")
    }
}