package com.example.playlistmaker.medialibrary.presentation.playlists

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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
import com.example.playlistmaker.common.utils.shareText
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.medialibrary.presentation.models.PlayListState
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

        playListViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlayListState.PlayListTracks -> {
                    showTracks(it.tracks)
                }
                is PlayListState.PlayListInfo -> {
                    playList = it.playList
                    showPlayList()
                }
            }
        }

        initOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
        playListViewModel.requestPlayListInfo(playList.playListId)

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
            if (playListViewModel.clickDebounce()) {
                shareText(buildShareText(), requireContext())
            }
        }

        binding.iconMore.setOnClickListener {
            if (playListViewModel.clickDebounce()) {
                PlayListBottomSheetFragment.newInstance(playList, buildShareText())
                    .show(childFragmentManager, PlayListBottomSheetFragment.TAG)
            }
        }
    }

    private fun buildShareText(): String {
        var shareText =
            "${playList.name}\n${playList.description}\n" + binding.playListInfoCountTracks.resources.getQuantityString(
                R.plurals.plural_count_tracks,
                playListTracksAdapter.tracks.size,
                playListTracksAdapter.tracks.size
            ) + "\n"
        playListTracksAdapter.tracks.forEachIndexed { index, track ->
            shareText += "\n ${index + 1}. ${track.artistName} - ${track.trackName} (" + SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(track.trackTimeMillis) + ")"
        }
        return shareText
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
            playListName.isSelected = true

            if (playList.description.isNotEmpty()) {
                playListDescription.text = playList.description
                playListDescription.isSelected = true
                playListDescription.visibility = View.VISIBLE
            } else {
                playListDescription.visibility = View.GONE
            }

            val bottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)
            binding.bottomBlankView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        bottomSheetBehavior.peekHeight = binding.bottomBlankView.height
                        binding.bottomBlankView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            )

        }
    }

    private fun showTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.playlist_is_empty), Toast.LENGTH_SHORT).show()
        }
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
            setNegativeButton(resources.getText(R.string.cancel)) { _, _ ->
            }
            setPositiveButton(resources.getText(R.string.delete)) { _, _ ->
                playListViewModel.deleteTrackFromPlaylist(track.trackId, playList.playListId)
            }
        }
        confirmDialog.show()
    }
}