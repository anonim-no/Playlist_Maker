package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.PLAYLIST_MAKER_PREFERENCE
import com.example.playlistmaker.TRACK
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.PlayerActivity
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val tracksInteractor = Creator.provideTracksInteractor(this)

    private val searchRunnable = Runnable { search() }

    private var searchInputQuery = ""

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    enum class Content {
        SEARCH_RESULT, NOT_FOUND, ERROR, TRACKS_HISTORY, PROGRESS_BAR
    }

    private val searchAdapter = TracksAdapter {
        clickOnTrack(it)
    }

    private val historyAdapter = TracksAdapter {
        clickOnTrack(it)
    }

//    private lateinit var toolbar: Toolbar
//    private lateinit var searchInput: EditText
//    private lateinit var searchClearInputButton: ImageView
//    private lateinit var rvSearchResults: RecyclerView
//    private lateinit var rvTracksHistory: RecyclerView
//    private lateinit var placeholderNotFound: TextView
//    private lateinit var placeholderError: LinearLayout
//    private lateinit var errorText: TextView
//    private lateinit var errorButton: Button
//    private lateinit var youSearched: LinearLayout
//    private lateinit var clearHistoryButton: Button
//    private lateinit var progressBar: ProgressBar


    private lateinit var tracksHistory: TracksHistory

    private val searchInputTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // при изменении текста скрываем или показываем кнопку очистки формы
            binding.clearSearchFormButton.visibility = clearButtonVisibility(s)
            // сохраняем тест в переменную
            searchInputQuery = s.toString()
            // если начали заполнять поле ввода - скрываем историю треков
            if (binding.inputSearchForm.hasFocus() && searchInputQuery.isNotEmpty()) {
                showContent(Content.SEARCH_RESULT)
            }
            // выполняем поиск автоматически через две секунды, после последних изменений
            searchDebounce()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // по клику назад закрываем SearchActivity и возвращаемся на предыдущее
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        //  по клику на корестике очищаем форму и результаты поиска
        binding.clearSearchFormButton.setOnClickListener {
            clearSearch()
        }

        // по клику на кнопке очистки истории поиска - очищаем историю поиска
        binding.clearHistoryButton.setOnClickListener {
            clearTracksHistory()
        }

        // к форме поиска добавляем обработчик ввода текста
        binding.inputSearchForm.addTextChangedListener(searchInputTextWatcher)

        binding.inputSearchForm.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputSearchForm.text.isEmpty()) {
                showContent(Content.SEARCH_RESULT)
            }
        }

        // при запуске скрываем или показываем кнопку очистки формы
        binding.clearSearchFormButton.visibility = clearButtonVisibility(searchInputQuery)

        // ставим фокус на форму поиска
        binding.inputSearchForm.requestFocus()

        // настраиваем адаптер для поиска
        binding.rvSearchResults.adapter = searchAdapter
        // если нажата кнопка done на клавиатуре - ищем
        binding.inputSearchForm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }
        // нажатие кнопки Обновить на экране с ошибкой повторяет поиск
        binding.errorButton.setOnClickListener {
            search()
        }

        // настраиваем адаптер для истории треков
        binding.rvTracksHistory.adapter = historyAdapter
        tracksHistory = TracksHistory(getSharedPreferences(PLAYLIST_MAKER_PREFERENCE, MODE_PRIVATE))
        // если поле поиска пустое - показываем историю треков
        if (binding.inputSearchForm.text.isEmpty()) {
            historyAdapter.tracks = tracksHistory.get()
            if (historyAdapter.tracks.isNotEmpty()) {
                showContent(Content.TRACKS_HISTORY)
            }
        }
    }

    private fun clickOnTrack(track: Track) {
        if (clickDebounce()) {
            tracksHistory.add(track)
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra(TRACK, Gson().toJson(track))
            }
            startActivity(intent)
        }
    }

    // функция берет строку поиска, делает запрос в апи и показывает результат
    private fun search() {
        if (searchInputQuery.isNotEmpty()) {

            // если пользователь нажал на кнопку done на клавиатуре до того как отработал автоматический поиск - отменяем его
            handler.removeCallbacks(searchRunnable)
            showContent(Content.PROGRESS_BAR)

            tracksInteractor.searchTracks(searchInputQuery, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {
                    handler.post {
                        if (foundTracks!=null) {
                            if (foundTracks.isEmpty()) {
                                // ничего не найдено, показываем соответствующий плейсхолдер
                                showContent(Content.NOT_FOUND)
                            } else {
                                // показываем результаты поиска
                                searchAdapter.tracks = foundTracks
                                showContent(Content.SEARCH_RESULT)
                            }
                        } else if (errorMessage != null) {
                            binding.errorText.text = errorMessage
                            showContent(Content.ERROR)
                        }
                    }
                }

            })
        }
    }

    // показывает нужный контент
    private fun showContent(content: Content) {
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

            Content.PROGRESS_BAR -> {
                binding.youSearched.visibility = View.GONE
                binding.placeholderNotFound.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
                binding.rvSearchResults.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    // очищаем историю
    private fun clearTracksHistory() {
        tracksHistory.clear()
        showContent(Content.SEARCH_RESULT)
        Toast.makeText(applicationContext, "История очищена", Toast.LENGTH_SHORT).show()
    }

    private fun clearSearch() {
        // обнуляем поле ввода
        binding.inputSearchForm.setText("")
        // показываем контент
        historyAdapter.tracks = tracksHistory.get()
        if (historyAdapter.tracks.isNotEmpty()) {
            showContent(Content.TRACKS_HISTORY)
        } else {
            showContent(Content.SEARCH_RESULT)
        }
        // Прячем клавиатуру
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    // перед уничтожением активити сохраняем всё что введено в поле поискового запроса
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchInputQuery)
    }

    // восстанавливаем текст и помещаем в EditText
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInputQuery = savedInstanceState.getString(SEARCH_QUERY, "")
        if (searchInputQuery.isNotEmpty()) {
            // восстанавливаем состояние после восстановления
            binding.inputSearchForm.setText(searchInputQuery)
            search()
        }
    }

    companion object {
        private const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}