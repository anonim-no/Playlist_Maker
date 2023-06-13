package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.playlistmaker.R
import com.example.playlistmaker.TRACK
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.models.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val viewModel by viewModel<SearchViewModel>()

    private val searchAdapter = TracksAdapter {
        clickOnTrack(it)
    }

    private val historyAdapter = TracksAdapter {
        clickOnTrack(it)
    }

    enum class Content {
        SEARCH_RESULT, NOT_FOUND, ERROR, TRACKS_HISTORY, LOADING
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // подписываемся на изменения состояния
        viewModel.observeState().observe(this) {
            render(it)
        }

        // подписываемся на тосты
        viewModel.observeShowToast().observe(this) {
            showToast(it)
        }

        // адаптер для поиска
        binding.rvSearchResults.adapter = searchAdapter
        // адаптер для истории треков
        binding.rvTracksHistory.adapter = historyAdapter

        // по клику назад закрываем SearchActivity и возвращаемся на предыдущее
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        //  по клику на крестике очищаем форму и результаты поиска
        binding.clearSearchFormButton.setOnClickListener {
            clearSearch()
        }

        // к форме поиска добавляем обработчик ввода текста
        binding.inputSearchForm.doOnTextChanged { s: CharSequence?, _, _, _ ->
            // при изменении текста скрываем или показываем кнопку очистки формы
            binding.clearSearchFormButton.visibility = clearButtonVisibility(s)
            // если начали заполнять поле ввода - скрываем историю треков
            if (binding.inputSearchForm.hasFocus() && s.toString().isNotEmpty()) {
                showState(Content.SEARCH_RESULT)
            }
            // выполняем поиск автоматически через две секунды, после последних изменений
            viewModel.searchDebounce(binding.inputSearchForm.text.toString())
        }

        // если нажата кнопка done на клавиатуре - ищем
        binding.inputSearchForm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.inputSearchForm.text.toString())
            }
            false
        }

        // нажатие кнопки Обновить на экране с ошибкой повторяет поиск
        binding.errorButton.setOnClickListener {
            viewModel.search(binding.inputSearchForm.text.toString())
        }

        // по клику на кнопке очистки истории поиска - очищаем историю поиска
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearTracksHistory(getString(R.string.history_was_clear))
        }

        // при запуске скрываем или показываем кнопку очистки формы
        binding.clearSearchFormButton.visibility =
            clearButtonVisibility(binding.inputSearchForm.text)

        // ставим фокус на форму поиска
        binding.inputSearchForm.requestFocus()
    }

    private fun clearSearch() {
        searchAdapter.tracks = arrayListOf()
        binding.inputSearchForm.setText("")
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        viewModel.clearSearch()
    }

    private fun clickOnTrack(track: Track) {
        if (viewModel.clickDebounce()) {
            viewModel.addTracksHistory(track)
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra(TRACK, track)
            }
            startActivity(intent)
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.SearchResult -> {
                searchAdapter.tracks = state.tracks
                showState(Content.SEARCH_RESULT)
            }

            is SearchState.History -> {
                historyAdapter.tracks = state.tracks
                showState(Content.TRACKS_HISTORY)
            }

            is SearchState.Error -> {
                binding.errorText.text = state.message
                showState(Content.ERROR)
            }

            is SearchState.NotFound -> showState(Content.NOT_FOUND)
            is SearchState.Loading -> showState(Content.LOADING)

        }
    }

    private fun showState(content: Content) {
        when (content) {
            Content.NOT_FOUND -> {
                binding.rvSearchResults.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.youSearched.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.placeholderNotFound.visibility = View.VISIBLE
            }

            Content.ERROR -> {
                binding.rvSearchResults.visibility = View.GONE
                binding.placeholderNotFound.visibility = View.GONE
                binding.youSearched.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.placeholderError.visibility = View.VISIBLE
            }

            Content.TRACKS_HISTORY -> {
                binding.rvSearchResults.visibility = View.GONE
                binding.placeholderNotFound.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.youSearched.visibility = View.VISIBLE
            }

            Content.SEARCH_RESULT -> {
                binding.youSearched.visibility = View.GONE
                binding.placeholderNotFound.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.rvSearchResults.visibility = View.VISIBLE
            }

            Content.LOADING -> {
                binding.youSearched.visibility = View.GONE
                binding.placeholderNotFound.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.rvSearchResults.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

}