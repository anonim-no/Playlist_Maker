package com.example.playlistmaker.medialibrary.presentation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import com.example.playlistmaker.medialibrary.presentation.models.PlayListsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding

    private val playListsViewModel: PlayListsViewModel by viewModel()

    private val playListsAdapter = PlayListsAdapter {
        clickOnPlayList(it)
    }

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

        binding.playListsRV.adapter = playListsAdapter

        binding.addPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_addPlayListFragment
            )
        }

        playListsViewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlayListsState.Empty -> {
                    binding.playListsRV.visibility = View.GONE
                    binding.placeholderNotFound.visibility = View.VISIBLE
                }

                is PlayListsState.PlayLists -> {
                    playListsAdapter.playLists = it.playLists
                    binding.placeholderNotFound.visibility = View.GONE
                    binding.playListsRV.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playListsViewModel.getPlayLists()
    }

    private fun clickOnPlayList(playList: PlayList) {
        if (playListsViewModel.clickDebounce()) {
            //TODO("implement")
        }
    }

    companion object {
        fun newInstance() = PlayListsFragment()
    }

}