package com.example.playlistmaker.medialibrary.domain.db.playlists

import android.net.Uri
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.common.models.PlayList

interface PlayListsInteractor {

    suspend fun addPlayList(playListName: String, playListDescription: String, pickImageUri: Uri?)

    suspend fun addTrackToPlayList(track: Track, playListId: Int)

    suspend fun getPlayList(playListId: Int): PlayList

    suspend fun getPlayLists(): List<PlayList>

    suspend fun getPlayListTracks(playListId: Int): List<Track>

    suspend fun isTrackInPlayList(trackId: Int, playListId: Int): Boolean

    suspend fun deleteTrackFromPlaylist(trackId: Int, playListId: Int)

    suspend fun deletePlaylist(playList: PlayList)

}