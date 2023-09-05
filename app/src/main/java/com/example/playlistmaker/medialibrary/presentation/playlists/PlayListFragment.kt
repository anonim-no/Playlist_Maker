package com.example.playlistmaker.medialibrary.presentation.playlists

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.concurrent.TimeUnit

class PlayListFragment : Fragment() {

    private val playListViewModel: PlayListViewModel by viewModel()

    private lateinit var binding: FragmentPlaylistBinding

    private lateinit var playList: PlayList

    private val playListTracksAdapter = TracksAdapter {
        clickOnTrack(it)
    }

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

        playListViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlayListState.PlayList -> {
                    playListTracksAdapter.tracks = it.tracks
                    showPlayList()
                }
            }
        }

        initOnClickListeners()

        playListViewModel.requestTracks(playList.playListId)

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

            if (playListTracksAdapter.tracks.isNotEmpty()) {
                var durationSum = 0L
                playListTracksAdapter.tracks.forEach { track ->
                    durationSum += track.trackTimeMillis ?: 0
                }
                durationSum = TimeUnit.MILLISECONDS.toMinutes(durationSum)
                playListInfoDuration.text = playListInfoDuration.resources.getQuantityString(
                    R.plurals.plural_minutes,
                    durationSum.toInt(),
                    durationSum
                )

                playListInfoCountTracks.text = playListInfoCountTracks.resources.getQuantityString(
                    R.plurals.plural_count_tracks,
                    playListTracksAdapter.tracks.size,
                    playListTracksAdapter.tracks.size
                )

                playListInfo.visibility = View.VISIBLE
            }
        }
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

    companion object {
        fun newInstance() = PlayListFragment()
    }

}