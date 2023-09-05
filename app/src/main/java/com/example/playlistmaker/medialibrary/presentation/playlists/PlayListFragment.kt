package com.example.playlistmaker.medialibrary.presentation.playlists

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R

class PlayListFragment : Fragment() {

    companion object {
        fun newInstance() = PlayListFragment()
    }

    private lateinit var viewModel: PlayListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlayListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}