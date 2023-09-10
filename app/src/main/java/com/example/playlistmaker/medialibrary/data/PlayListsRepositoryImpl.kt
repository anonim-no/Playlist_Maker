package com.example.playlistmaker.medialibrary.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.common.PLAY_LISTS_IMAGES_DIRECTORY
import com.example.playlistmaker.common.PLAY_LISTS_IMAGES_QUALITY
import com.example.playlistmaker.common.models.PlayList
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.data.db.AppDatabase
import com.example.playlistmaker.medialibrary.data.db.playlists.converters.PlayListsTrackDbConvertor
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListWithCountTracks
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.PlayListsTrackEntity
import com.example.playlistmaker.medialibrary.data.db.playlists.entity.TrackPlayListEntity
import com.example.playlistmaker.medialibrary.domain.db.playlists.PlayListsRepository
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class PlayListsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playListsTrackDbConvertor: PlayListsTrackDbConvertor,
    private val context: Context
) : PlayListsRepository {

    private val albumImagesFilePath = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        PLAY_LISTS_IMAGES_DIRECTORY
    )

    override suspend fun addPlayList(playListName: String, playListDescription: String, pickImageUri: Uri?) {
        var imageFileName: String? = null
        if (pickImageUri != null) {
            imageFileName = saveAlbumImage(pickImageUri)
        }
        appDatabase.playListsTrackDao().addPlayList(
            PlayListEntity(
                null,
                playListName,
                playListDescription,
                imageFileName
            )
        )
    }

    override suspend fun editPlayList(
        playListId: Int,
        playListName: String,
        playListDescription: String,
        pickImageUri: Uri?
    ) {
        val playList = appDatabase.playListsTrackDao().getPlayList(playListId)

        var imageFileName = playList.image
        if (pickImageUri != null) {
            if (playList.image != null) {
                deleteAlbumImage(playList.image)
            }
            imageFileName = saveAlbumImage(pickImageUri)
        }

        appDatabase.playListsTrackDao().editPlayList(
            PlayListEntity(
                playListId,
                playListName,
                playListDescription,
                imageFileName
            )
        )
    }

    override suspend fun addTrackToPlayList(track: Track, playListId: Int) =
        appDatabase.playListsTrackDao().addTrackToPlayList(
            playListsTrackEntity = playListsTrackDbConvertor.map(track),
            trackPlayListEntity = TrackPlayListEntity(null, playListId, track.trackId)
        )

    override suspend fun getPlayList(playListId: Int): PlayList =
        playListsTrackDbConvertor.map(
            appDatabase.playListsTrackDao().getPlayList(playListId)
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

    override suspend fun deletePlaylist(playList: PlayList) {
        playList.image?.let {
            deleteAlbumImage(it)
        }
        appDatabase.playListsTrackDao().deletePlayList(playList.playListId)
    }

    private fun deleteAlbumImage(imageFileName: String) {
        if (File(albumImagesFilePath, imageFileName).exists()) {
            File(albumImagesFilePath, imageFileName).delete()
        }
    }

    private fun saveAlbumImage(uri: Uri): String {
        val imageFileName = Calendar.getInstance().timeInMillis.toString() + ".jpg"
        if (!albumImagesFilePath.exists()) {
            albumImagesFilePath.mkdirs()
        }
        val file = File(albumImagesFilePath, imageFileName)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, PLAY_LISTS_IMAGES_QUALITY, outputStream)
        return imageFileName
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