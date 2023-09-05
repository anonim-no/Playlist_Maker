package com.example.playlistmaker.medialibrary.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.TRACK
import com.example.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.example.playlistmaker.medialibrary.presentation.models.FavoritesTracksState
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.common.presentation.TracksAdapter
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
            findNavController().navigate(
                R.id.action_to_PlayerFragment,
                Bundle().apply {
                    putSerializable(TRACK, track)
                }
            )
        }
    }

    companion object {
        fun newInstance() = FavoritesTracksFragment()
    }

}