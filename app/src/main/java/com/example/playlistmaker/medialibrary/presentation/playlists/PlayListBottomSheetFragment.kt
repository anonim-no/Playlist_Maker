package com.example.playlistmaker.medialibrary.presentation.playlists

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.PLAY_LIST
import com.example.playlistmaker.common.PLAY_LISTS_IMAGES_DIRECTORY
import com.example.playlistmaker.common.models.PlayList
import com.example.playlistmaker.common.utils.shareText
import com.example.playlistmaker.databinding.FragmentPlaylistBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlayListBottomSheetFragment(private val playList: PlayList, private val shareText: String) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPlaylistBottomSheetBinding

    private val viewModelPlayListBottomSheet by viewModel<PlayListBottomSheetViewModel>()

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAY_LISTS_IMAGES_DIRECTORY
        )
        Glide
            .with(binding.itemPlaylist.playListImage)
            .load(playList.image?.let { imageName -> File(filePath, imageName) })
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.itemPlaylist.playListImage)

        binding.itemPlaylist.playListName.text = playList.name
        binding.itemPlaylist.playListTracksCount.text =
            binding.itemPlaylist.playListTracksCount.resources.getQuantityString(
                R.plurals.plural_count_tracks, playList.tracksCount, playList.tracksCount
            )

        binding.buttonSharing.setOnClickListener {
            if (viewModelPlayListBottomSheet.clickDebounce()) {
                if (playList.tracksCount>0) {
                    shareText(shareText, requireContext())
                } else {
                    dismiss()
                    Toast.makeText(requireContext(), getString(R.string.playlist_is_empty_for_share), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.buttonEdit.setOnClickListener {
            if (viewModelPlayListBottomSheet.clickDebounce()) {
                dismiss()
                findNavController().navigate(
                    R.id.action_to_addPlayListFragment,
                    Bundle().apply {
                        putSerializable(PLAY_LIST, playList)
                    }
                )
            }
        }

        binding.buttonDelete.setOnClickListener {
            if (viewModelPlayListBottomSheet.clickDebounce()) {
                confirmDialog = MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(resources.getText(R.string.playlist_delete))
                    setMessage(resources.getText(R.string.playlist_delete_q))
                    setNegativeButton(resources.getText(R.string.no)) { _, _ ->
                    }
                    setPositiveButton(resources.getText(R.string.yes)) { _, _ ->

                        viewModelPlayListBottomSheet.deletePlaylist(
                            playList
                        ) {
                            Toast.makeText(
                                requireContext(),
                                resources.getText(R.string.playlist_deleted),
                                Toast.LENGTH_SHORT
                            ).show()

                            findNavController().popBackStack()
                        }
                    }
                }
                confirmDialog.show()
            }
        }
    }

    companion object {
        const val TAG = "PlaylistBottomSheet"

        fun newInstance(playList: PlayList, shareText: String): PlayListBottomSheetFragment {
            return PlayListBottomSheetFragment(playList, shareText)
        }
    }
}