package com.example.playlistmaker.player.presentation

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.TRACK
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.presentation.models.PlayerState
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import com.example.playlistmaker.player.presentation.models.PlayListsState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<PlayerViewModel>()

    private val playListsAdapter = PlayListsAdapter {
        addTrackToPlayList(it)
    }

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playListsRV.adapter = playListsAdapter

        val bottomSheetContainer = findViewById<LinearLayout>(R.id.player_bottom_sheet)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        viewModel.observePlayerStateState().observe(this) {
            render(it)
        }

        viewModel.observeTrackTimeState().observe(this) {
            render(it)
        }

        viewModel.observeFavoriteState().observe(this) {
            render(it)
        }

        viewModel.observePlayListsState().observe(this) {
            when (it) {
                is PlayListsState.Empty -> binding.playListsRV.visibility = View.GONE
                is PlayListsState.PlayLists -> {
                    playListsAdapter.playLists = it.playLists
                    binding.playListsRV.visibility = View.VISIBLE
                }
            }
        }

        binding.playlistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra(TRACK) as Track

        showTrack(track)

        viewModel.isFavorite(track.trackId)

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        binding.playButton.isEnabled = false

        if (savedInstanceState == null) {
            viewModel.preparePlayer(track.previewUrl)
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlayLists()
    }

    private fun addTrackToPlayList(playList: PlayList) {
        viewModel.addTrackToPlayList(track, playList)
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
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

}