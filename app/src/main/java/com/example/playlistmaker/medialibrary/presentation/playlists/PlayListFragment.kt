package com.example.playlistmaker.medialibrary.presentation.playlists

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.PLAY_LIST
import com.example.playlistmaker.common.PLAY_LISTS_IMAGES_DIRECTORY
import com.example.playlistmaker.common.TRACK
import com.example.playlistmaker.common.models.PlayList
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.common.presentation.TracksAdapter
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.medialibrary.presentation.models.PlayListState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class PlayListFragment : Fragment() {

    private val playListViewModel: PlayListViewModel by viewModel()

    private lateinit var binding: FragmentPlaylistBinding

    private lateinit var playList: PlayList

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val playListTracksAdapter = TracksAdapter(
        {
            clickOnTrack(it)
        },
        {
            longClickOnTrack(it)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tracksRV.adapter = playListTracksAdapter

        showPlayList()

        //TODO("Размер")

        playListViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlayListState.PlayList -> showTracks(it.tracks)
            }
        }

        initOnClickListeners()


    }

    override fun onResume() {
        super.onResume()
        playListViewModel.requestTracks(playList.playListId)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        playList = arguments?.getSerializable(PLAY_LIST) as PlayList
    }

    private fun initOnClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.iconShare.setOnClickListener {
            sharePlayList()
        }
    }

    private fun sharePlayList() {
        var shareText = "${playList.name}\n${playList.description}\n" + binding.playListInfoCountTracks.resources.getQuantityString(
            R.plurals.plural_count_tracks,
            playListTracksAdapter.tracks.size,
            playListTracksAdapter.tracks.size
        ) + "\n"
        playListTracksAdapter.tracks.forEachIndexed { index, track ->
            shareText += "\n ${index+1}. ${track.artistName} - ${track.trackName} (" + SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis) + ")"
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        intent.type = "text/plain"
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                getString(R.string.settings_not_found_app),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showPlayList() {
        binding.apply {

            val filePath = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                PLAY_LISTS_IMAGES_DIRECTORY
            )
            Glide
                .with(playListImage)
                .load(playList.image?.let { imageName -> File(filePath, imageName) })
                .placeholder(R.drawable.ic_placeholder)
                .into(playListImage)

            playListName.text = playList.name

            if (playList.description.isNotEmpty()) {
                playListDescription.text = playList.description
                playListDescription.visibility = View.VISIBLE
            }

        }
    }

    private fun showTracks(tracks: List<Track>) {
        playListTracksAdapter.tracks = tracks
        var durationSum = 0L
        playListTracksAdapter.tracks.forEach { track ->
            durationSum += track.trackTimeMillis ?: 0
        }
        durationSum = TimeUnit.MILLISECONDS.toMinutes(durationSum)
        binding.playListInfoDuration.text = binding.playListInfoDuration.resources.getQuantityString(
            R.plurals.plural_minutes,
            durationSum.toInt(),
            durationSum
        )

        binding.playListInfoCountTracks.text = binding.playListInfoCountTracks.resources.getQuantityString(
            R.plurals.plural_count_tracks,
            playListTracksAdapter.tracks.size,
            playListTracksAdapter.tracks.size
        )
    }

    private fun clickOnTrack(track: Track) {
        if (playListViewModel.clickDebounce()) {
            findNavController().navigate(
                R.id.action_to_PlayerFragment,
                Bundle().apply {
                    putSerializable(TRACK, track)
                }
            )
        }
    }
    private fun longClickOnTrack(track: Track) {
        confirmDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(resources.getText(R.string.delete_track))
            setNegativeButton(resources.getText(R.string.cancel)) { dialog, which ->
            }
            setPositiveButton(resources.getText(R.string.delete)) { dialog, which ->
                playListViewModel.deleteTrackFromPlaylist(track.trackId, playList.playListId)
            }
        }
        confirmDialog.show()
    }
}