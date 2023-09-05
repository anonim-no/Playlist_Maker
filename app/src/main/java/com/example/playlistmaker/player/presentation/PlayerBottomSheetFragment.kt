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
import com.example.playlistmaker.common.models.PlayList
import com.example.playlistmaker.common.presentation.PlayListViewHolder
import com.example.playlistmaker.common.presentation.PlayListsAdapter
import com.example.playlistmaker.player.presentation.models.PlayListsState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerBottomSheetFragment(val track: Track) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBinding

    private val viewModelPlayerBottomSheet by viewModel<PlayerBottomSheetViewModel>()

    private val playListsAdapter = object : PlayListsAdapter(
        clickListener = {
            addTrackToPlayList(it)
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
            return PlayListViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
            )
        }
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
                        dismiss()
                    } else {
                        showToast(getString(R.string.already_added_to_playlist, it.playListName))
                    }
                }
            }
        }

        binding.addPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_to_addPlayListFragment
            )
        }

    }

    override fun onResume() {
        super.onResume()
        viewModelPlayerBottomSheet.requestPlayLists()
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
        const val TAG = "PlayerBottomSheet"

        fun newInstance(track: Track): PlayerBottomSheetFragment {
            return PlayerBottomSheetFragment(track)
        }
    }
}