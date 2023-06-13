package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.TRACK
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra(TRACK) as Track

        showTrack(track)

        binding.playButton.isEnabled = false

        viewModel.preparePlayer(track.previewUrl)

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
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
                binding.playButton.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Playing -> {
                binding.playButton.setImageResource(R.drawable.ic_pause)
            }

            is PlayerState.UpdatePlayingTime -> {
                binding.playingTime.text = state.playingTime
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