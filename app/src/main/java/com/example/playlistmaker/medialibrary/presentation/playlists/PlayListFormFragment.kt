package com.example.playlistmaker.medialibrary.presentation.playlists

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.PLAY_LIST
import com.example.playlistmaker.common.PLAY_LISTS_IMAGES_DIRECTORY
import com.example.playlistmaker.common.models.PlayList
import com.example.playlistmaker.databinding.FragmentPlaylistFormBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlayListFormFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistFormBinding

    private val playListFormViewModel by viewModel<PlayListFormViewModel>()

    private var playList: PlayList? = null

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var pickImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playList?.let {
            binding.toolbar.title = "Редактировать"
            binding.playListCreateButton.text = "Сохранить"
            binding.playListNameEditText.setText(it.name)
            binding.playListDescriptionEditText.setText(it.description)
            it.image?.let { imageName ->
                val filePath = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    PLAY_LISTS_IMAGES_DIRECTORY
                )
                val file = File(filePath, imageName)
                binding.addImage.setImageURI(file.toUri())
            }
            binding.playListCreateButton.isEnabled = true
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.finish_creating_playlist))
            setMessage(getString(R.string.all_unsaved_data_will_be_lost))
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().navigateUp()
            }
        }

        binding.toolbar.setOnClickListener {
            if (checkUnsavedData()) {
                confirmDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.playListNameEditText.doOnTextChanged { s: CharSequence?, _, _, _ ->
            binding.playListCreateButton.isEnabled = !s.isNullOrEmpty()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.addImage.setImageURI(uri)
                    pickImageUri = uri
                }
            }
        binding.addImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playListCreateButton.setOnClickListener {
            val name = binding.playListNameEditText.text.toString()
            val description = binding.playListDescriptionEditText.text.toString()
            if (name.isNotEmpty()) {
                if (playList != null) {
                    playListFormViewModel.editPlayList(
                        playList!!.playListId,
                        name = name,
                        description = description,
                        pickImageUri = pickImageUri
                    ) {
                        findNavController().popBackStack()
                    }
                } else {
                    playListFormViewModel.createPlayList(
                        name = name,
                        description = description,
                        pickImageUri = pickImageUri
                    ) {
                        Toast.makeText(
                            requireContext(),
                            String.format(resources.getText(R.string.playlist_created).toString(), name),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }

                }
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        playList = arguments?.getSerializable(PLAY_LIST) as PlayList?

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (checkUnsavedData()) {
                    confirmDialog.show()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun checkUnsavedData(): Boolean {
        playList?.let {
            return false
        }
        return (
                pickImageUri != null
                || binding.playListNameEditText.text.toString().isNotEmpty()
                || binding.playListDescriptionEditText.text.toString().isNotEmpty()
                )
    }

}