package com.example.playlistmaker.medialibrary.presentation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.medialibrary.presentation.models.PlayListsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding

    private val playListsViewModel: PlayListsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_addPlayListFragment
            )
        }

        playListsViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlayListsState.Empty -> {
                    binding.placeholderNotFound.visibility = View.VISIBLE
                }

                is PlayListsState.PlayLists -> {

                }
            }
        }
    }

    companion object {
        fun newInstance() = PlayListsFragment()
    }

}