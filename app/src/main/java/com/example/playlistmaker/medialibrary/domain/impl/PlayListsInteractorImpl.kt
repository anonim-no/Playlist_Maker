package com.example.playlistmaker.medialibrary.domain.impl

import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsInteractor
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsRepository
import com.example.playlistmaker.common.models.PlayList

class PlayListsInteractorImpl(private val playListsRepository: PlayListsRepository) : PlayListsInteractor {
    override suspend fun addPlayList(playList: PlayList) =
        playListsRepository.addPlayList(playList)

    override suspend fun addTrackToPlayList(track: Track, playListId: Int) =
        playListsRepository.addTrackToPlayList(track, playListId)

    override suspend fun getPlayLists(): List<PlayList> =
        playListsRepository.getPlayLists()

    override suspend fun getPlayListTracks(playListId: Int): List<Track> =
        playListsRepository.getPlayListTracks(playListId)

    override suspend fun isTrackInPlayList(trackId: Int, playListId: Int): Boolean =
        playListsRepository.isTrackInPlayList(trackId, playListId)

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playListId: Int) {
        playListsRepository.deleteTrackFromPlaylist(trackId, playListId)
    }
}