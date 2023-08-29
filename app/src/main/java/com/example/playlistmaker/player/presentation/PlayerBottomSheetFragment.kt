package com.example.playlistmaker.player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.databinding.FragmentBottomSheetBinding
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import com.example.playlistmaker.player.presentation.models.PlayListsState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerBottomSheetFragment(val track: Track) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBinding

    private val viewModelPlayerBottomSheet by viewModel<PlayerBottomSheetViewModel>()

    private val playListsAdapter = PlayListsAdapter {
        addTrackToPlayList(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playListsRV.adapter = playListsAdapter

        viewModelPlayerBottomSheet.observePlayListsState().observe(viewLifecycleOwner) {
            when (it) {
                is PlayListsState.Empty -> binding.playListsRV.visibility = View.GONE
                is PlayListsState.PlayLists -> {
                    playListsAdapter.playLists = it.playLists
                    binding.playListsRV.visibility = View.VISIBLE
                }
                is PlayListsState.AddTrackToPlayListResult -> {
                    if (it.isAdded) {
                        showToast(getString(R.string.added_to_playlist, it.playListName))
                    } else {
                        showToast(getString(R.string.already_added_to_playlist, it.playListName))
                    }
                    dismiss()
                }
            }
        }

        binding.addPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_playerFragment_to_addPlayListFragment
            )
        }

    }

    override fun onResume() {
        super.onResume()
        viewModelPlayerBottomSheet.getPlayLists()
    }

    private fun addTrackToPlayList(playList: PlayList) {
        if (viewModelPlayerBottomSheet.clickDebounce()) {
            viewModelPlayerBottomSheet.addTrackToPlayList(track, playList)
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val TAG = "ModalBottomSheet"

        fun newInstance(track: Track): PlayerBottomSheetFragment {
            return PlayerBottomSheetFragment(track)
        }
    }
}