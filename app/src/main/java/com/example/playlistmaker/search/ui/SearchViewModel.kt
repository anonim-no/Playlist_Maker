package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    private val showToast = SingleLiveEvent<String>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    fun observeShowToast(): LiveData<String> = showToast

    private var isClickAllowed = true

    // при старте активити показываем историю треков, если есть
    init {
        val historyTracks = getTracksHistory()
        if (historyTracks.isNotEmpty()) {
            renderState(SearchState.History(historyTracks))
        }
    }

    private val tracksSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { searchText ->
        search(searchText)
    }

    fun searchDebounce(searchText: String) {
        if (searchText.isNotEmpty()) {
            tracksSearchDebounce(searchText)
        }
    }

    fun search(searchText: String) {
        if (searchText.isNotEmpty()) {

            renderState(SearchState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .searchTracks(searchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: ArrayList<Track>?, errorMessage: String?) {
        val tracks = arrayListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(SearchState.Error(message = errorMessage))
            }

            tracks.isEmpty() -> {
                renderState(SearchState.NotFound)
            }

            else -> {
                renderState(SearchState.SearchResult(tracks = tracks))
            }
        }
    }

    fun clearSearch() {
        val historyTracks = getTracksHistory()
        if (historyTracks.isNotEmpty()) {
            renderState(SearchState.History(historyTracks))
        } else {
            renderState(SearchState.SearchResult(arrayListOf()))
        }
    }

    fun addTracksHistory(track: Track) {
        searchInteractor.addTracksHistory(track)
    }

    fun clearTracksHistory(text: String) {
        searchInteractor.clearTracksHistory()
        renderState(SearchState.SearchResult(arrayListOf()))
        showToast.postValue(text)
    }

    private fun getTracksHistory(): ArrayList<Track> {
        return searchInteractor.getTracksHistory()
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

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}























