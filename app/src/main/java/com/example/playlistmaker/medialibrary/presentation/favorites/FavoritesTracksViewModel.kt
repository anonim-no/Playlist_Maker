package com.example.playlistmaker.medialibrary.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.medialibrary.domain.db.favorites.FavoritesInteractor
import com.example.playlistmaker.medialibrary.presentation.models.FavoritesTracksState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavoritesTracksViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesTracksState>()
    fun observeState(): LiveData<FavoritesTracksState> = stateLiveData

    private var isClickAllowed = true

    fun requestFavoritesTracks() {
        viewModelScope.launch {
            favoritesInteractor
                .getFavoritesTracks()
                .collect { favoritesTracks ->
                    if (favoritesTracks.isEmpty()) {
                        renderState(FavoritesTracksState.Empty)
                    } else {
                        renderState(FavoritesTracksState.FavoritesTracks(favoritesTracks))
                    }
                }
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun renderState(state: FavoritesTracksState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}