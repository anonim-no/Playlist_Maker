package com.example.playlistmaker.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.TRACK
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    enum class PlayerState {
        STATE_DEFAULT, STATE_PREPARED, STATE_PLAYING, STATE_PAUSED
    }

    private var playerState = PlayerState.STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())

    private val updatePlayingTimeRunnable = Runnable { updatePlayingTime() }

    private lateinit var binding: ActivityPlayerBinding

    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.playButton.isEnabled = false

        val track = intent.getSerializableExtra(TRACK) as Track

        preparePlayer(track.previewUrl)

        binding.playButton.setOnClickListener {
            playbackControl()
        }

        Glide
            .with(binding.albumArt)
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
            .into(binding.albumArt)

        binding.trackName.text = track.trackName
        binding.trackName.isSelected = true
        binding.artistName.text = track.artistName
        binding.trackName.isSelected = true
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

        binding.trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(track.releaseDate)
        if (date != null) {
            val formattedDatesString = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            binding.releaseDate.text = formattedDatesString
        }

        if (track.collectionName.isNotEmpty()) {
            binding.collectionName.text = track.collectionName
        } else {
            binding.collectionName.visibility = View.GONE
            binding.collectionNameTitle.visibility = View.GONE
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playButton.isEnabled = true
            binding.playButton.setImageResource(R.drawable.ic_play)
            binding.progressBar.visibility = View.GONE
            playerState = PlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.playButton.setImageResource(R.drawable.ic_play)
            playerState = PlayerState.STATE_PREPARED
            binding.playingTime.setText(R.string._00_00)
            handler.removeCallbacks(updatePlayingTimeRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.ic_pause)
        playerState = PlayerState.STATE_PLAYING
        updatePlayingTime()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.ic_play)
        playerState = PlayerState.STATE_PAUSED
        handler.removeCallbacks(updatePlayingTimeRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_DEFAULT -> {}
        }
    }

    private fun updatePlayingTime() {
        binding.playingTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        handler.postDelayed(updatePlayingTimeRunnable, UPDATE_PLAYING_TIME_DELAY)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val UPDATE_PLAYING_TIME_DELAY = 500L
    }

}