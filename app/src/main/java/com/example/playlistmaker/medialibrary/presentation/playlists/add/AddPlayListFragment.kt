package com.example.playlistmaker.medialibrary.presentation.playlists.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentAddplaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddPlayListFragment : Fragment() {

    private lateinit var binding: FragmentAddplaylistBinding

    private val addPlayListViewModel: AddPlayListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnClickListener {
            // TODO: проверка на несохраненные данные и подверждение выхода
            findNavController().popBackStack()
        }

    }

    companion object {
        fun newInstance() = AddPlayListFragment()
    }


}