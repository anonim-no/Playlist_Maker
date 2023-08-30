package com.example.playlistmaker.player.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.TRACK
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.presentation.models.PlayerState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding

    private val playerViewModel by viewModel<PlayerViewModel>()

    private lateinit var track: Track

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showTrack(track)

        playerViewModel.preparePlayer(track.previewUrl)

        playerViewModel.isFavorite(track.trackId)

        initObserveViewModel()

        initOnClickListeners()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        track = arguments?.getSerializable(TRACK) as Track
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pausePlayer()
    }

    private fun initOnClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.playlistButton.setOnClickListener {
            PlayerBottomSheetFragment.newInstance(track).show(childFragmentManager, PlayerBottomSheetFragment.TAG)
        }
        binding.favoriteButton.setOnClickListener {
            playerViewModel.onFavoriteClicked(track)
        }
        binding.playButton.setOnClickListener {
            playerViewModel.playbackControl()
        }
    }

    private fun initObserveViewModel() {
        playerViewModel.observePlayerStateState().observe(viewLifecycleOwner) {
            render(it)
        }
        playerViewModel.observeTrackTimeState().observe(viewLifecycleOwner) {
            render(it)
        }
        playerViewModel.observeFavoriteState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Preparing -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            is PlayerState.Stopped -> {
                binding.playButton.isEnabled = true
                binding.progressBar.visibility = View.GONE
                binding.playButton.setImageResource(R.drawable.ic_play)
                binding.playingTime.setText(R.string._00_00)
            }

            is PlayerState.Unplayable -> {
                binding.playButton.isEnabled = false
                binding.progressBar.visibility = View.GONE
                binding.playButton.setImageResource(R.drawable.ic_play)
                binding.playingTime.setText(R.string._00_00)
            }

            is PlayerState.Paused -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Playing -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.ic_pause)
            }

            is PlayerState.UpdatePlayingTime -> {
                binding.playingTime.text = state.playingTime
            }

            is PlayerState.StateFavorite -> {
                if (state.isFavorite) {
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorited)
                } else {
                    binding.favoriteButton.setImageResource(R.drawable.ic_add_to_favorites)
                }
            }
        }
    }

    private fun showTrack(track: Track) {

        binding.apply {

            trackName.text = track.trackName
            trackName.isSelected = true

            track.artworkUrl100?.let {
                Glide
                    .with(albumArt)
                    .load(it.replaceAfterLast('/', "512x512bb.jpg"))
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .transform(
                        RoundedCorners(
                            resources.getDimensionPixelSize(
                                R.dimen.track_album_corner_radius
                            )
                        )
                    )
                    .into(albumArt)
            }


            track.artistName?.let {
                artistName.text = it
                trackName.isSelected = true
            }

            if (track.trackTimeMillis != null) {
                trackTime.text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            } else {
                trackTime.setText(R.string._00_00)
            }

            if (track.primaryGenreName != null) {
                primaryGenreName.text = track.primaryGenreName
            } else {
                country.visibility = View.GONE
                countryTitle.visibility = View.GONE
            }

            if (track.country != null) {
                country.text = track.country
            } else {
                country.visibility = View.GONE
                countryTitle.visibility = View.GONE
            }

            if (track.releaseDate != null) {
                val parseDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(track.releaseDate)
                releaseDate.text =
                    parseDate?.let { date ->
                        SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                    }
            } else {
                releaseDate.visibility = View.GONE
                releaseDateTitle.visibility = View.GONE
            }

            if (track.collectionName != null) {
                collectionName.text = track.collectionName
            } else {
                collectionName.visibility = View.GONE
                collectionNameTitle.visibility = View.GONE
            }
        }

        binding.playButton.isEnabled = false

    }
}