package com.example.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState

class SearchViewModel(application: Application) : AndroidViewModel(application) {


    private val searchInteractor = Creator.provideSearchInteractor(getApplication())

    private val stateLiveData = MutableLiveData<SearchState>()
    private val showToast = SingleLiveEvent<String>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    fun observeShowToast(): LiveData<String> = showToast

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    // при старте активити показываем историю треков, если есть
    init {
        val historyTracks = getTracksHistory()
        if (historyTracks.isNotEmpty()) {
            renderState(SearchState.History(historyTracks))
        }
    }

    fun searchDebounce(SearchText: String) {
        if (SearchText.isNotEmpty()) {
            val searchRunnable = Runnable { search(SearchText) }
            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
            handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )
        }
    }

    fun search(SearchText: String) {
        if (SearchText.isNotEmpty()) {

            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

            renderState(SearchState.Loading)

            searchInteractor.searchTracks(SearchText, object : SearchInteractor.SearchConsumer {
                override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {

                    val tracks = arrayListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                SearchState.Error(
                                    message = getApplication<Application>().getString(R.string.check_internet_connection),
                                )
                            )
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                SearchState.NotFound
                            )
                        }

                        else -> {
                            renderState(
                                SearchState.SearchResult(
                                    tracks = tracks,
                                )
                            )
                        }
                    }
                }
            })
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

    fun clearTracksHistory() {
        searchInteractor.clearTracksHistory()
        renderState(SearchState.SearchResult(arrayListOf()))
        showToast.postValue(getApplication<Application>().getString(R.string.history_was_clear))
    }

    private fun getTracksHistory(): ArrayList<Track> {
        return searchInteractor.getTracksHistory()
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}























