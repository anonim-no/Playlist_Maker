package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.TRACK
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
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
            Glide
                .with(albumArt)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
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

            trackName.text = track.trackName
            trackName.isSelected = true
            artistName.text = track.artistName
            trackName.isSelected = true
            primaryGenreName.text = track.primaryGenreName
            country.text = track.country

            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(track.releaseDate)
            if (date != null) {
                val formattedDatesString =
                    SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                releaseDate.text = formattedDatesString
            }

            if (track.collectionName.isNotEmpty()) {
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