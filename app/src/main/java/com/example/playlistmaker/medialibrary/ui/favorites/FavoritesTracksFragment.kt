package com.example.playlistmaker.medialibrary.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.TRACK
import com.example.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.example.playlistmaker.medialibrary.ui.models.FavoritesTracksState
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesTracksBinding

    private val favoritesTracksViewModel: FavoritesTracksViewModel by viewModel()

    private val favoritesTracksAdapter = TracksAdapter {
        clickOnTrack(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesTracksViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.rvFavoritesTracks.adapter = favoritesTracksAdapter

    }

    override fun onResume() {
        super.onResume()
        favoritesTracksViewModel.getFavoritesTracks()
    }

    private fun render(state: FavoritesTracksState) {
        when (state) {
            is FavoritesTracksState.Empty -> {
                binding.rvFavoritesTracks.visibility = View.GONE
                binding.placeholderNotFound.visibility = View.VISIBLE
            }

            is FavoritesTracksState.FavoritesTracks -> {
                favoritesTracksAdapter.tracks = state.tracks
                binding.placeholderNotFound.visibility = View.GONE
                binding.rvFavoritesTracks.visibility = View.VISIBLE
            }
        }
    }

    private fun clickOnTrack(track: Track) {
        if (favoritesTracksViewModel.clickDebounce()) {
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra(TRACK, track)
            }
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance() = FavoritesTracksFragment()
    }

}