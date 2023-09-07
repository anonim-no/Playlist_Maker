package com.example.playlistmaker.medialibrary.domain.impl

import android.net.Uri
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsRepository
import com.example.playlistmaker.common.models.PlayList

class PlayListsInteractorImpl(private val playListsRepository: PlayListsRepository) : PlayListsInteractor {
    override suspend fun addPlayList(playListName: String, playListDescription: String, pickImageUri: Uri?) =
        playListsRepository.addPlayList(playListName, playListDescription, pickImageUri)

    override suspend fun editPlayList(playListId: Int, playListName: String, playListDescription: String, pickImageUri: Uri?) {
        playListsRepository.editPlayList(playListId, playListName, playListDescription, pickImageUri)
    }

    override suspend fun addTrackToPlayList(track: Track, playListId: Int) =
        playListsRepository.addTrackToPlayList(track, playListId)

    override suspend fun getPlayList(playListId: Int): PlayList =
        playListsRepository.getPlayList(playListId)

    override suspend fun getPlayLists(): List<PlayList> =
        playListsRepository.getPlayLists()

    override suspend fun getPlayListTracks(playListId: Int): List<Track> =
        playListsRepository.getPlayListTracks(playListId)

    override suspend fun isTrackInPlayList(trackId: Int, playListId: Int): Boolean =
        playListsRepository.isTrackInPlayList(trackId, playListId)

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playListId: Int) =
        playListsRepository.deleteTrackFromPlaylist(trackId, playListId)

    override suspend fun deletePlaylist(playList: PlayList) =
        playListsRepository.deletePlaylist(playList)

}