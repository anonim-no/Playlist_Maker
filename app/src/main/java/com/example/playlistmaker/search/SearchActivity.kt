package com.example.playlistmaker.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.*
import com.example.playlistmaker.models.Track
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val searchRunnable = Runnable { search() }

    private var searchInputQuery = ""

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    enum class Content {
        SEARCH_RESULT, NOT_FOUND, ERROR, TRACKS_HISTORY, PROGRESS_BAR
    }

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(API::class.java)

    private val searchAdapter = TracksAdapter {
        clickOnTrack(it)
    }

    private val historyAdapter = TracksAdapter {
        clickOnTrack(it)
    }

    private lateinit var toolbar: Toolbar
    private lateinit var searchInput: EditText
    private lateinit var searchClearInputButton: ImageView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var rvTracksHistory: RecyclerView
    private lateinit var placeholderNotFound: TextView
    private lateinit var placeholderError: LinearLayout
    private lateinit var errorButton: Button
    private lateinit var youSearched: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var tracksHistory: TracksHistory

    private val searchInputTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // при изменении текста скрываем или показываем кнопку очистки формы
            searchClearInputButton.visibility = clearButtonVisibility(s)
            // сохраняем тест в переменную
            searchInputQuery = s.toString()
            // если начали заполнять поле ввода - скрываем историю треков
            if (searchInput.hasFocus() && searchInputQuery.isNotEmpty()) {
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
        setContentView(R.layout.activity_search)

        toolbar = findViewById(R.id.toolbar)
        searchInput = findViewById(R.id.inputSearchForm)
        searchClearInputButton = findViewById(R.id.clearSearchForm)
        rvSearchResults = findViewById(R.id.rvSearchResults)
        rvTracksHistory = findViewById(R.id.rvTracksHistory)
        placeholderNotFound = findViewById(R.id.placeholderNotFound)
        placeholderError = findViewById(R.id.placeholderError)
        errorButton = findViewById(R.id.errorButton)
        youSearched = findViewById(R.id.youSearched)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        // по клику назад закрываем SearchActivity и возвращаемся на предыдущее
        toolbar.setNavigationOnClickListener {
            finish()
        }

        //  по клику на корестике очищаем форму и результаты поиска
        searchClearInputButton.setOnClickListener {
            clearSearch()
        }

        // по клику на кнопке очистки истории поиска - очищаем историю поиска
        clearHistoryButton.setOnClickListener {
            clearTracksHistory()
        }

        // к форме поиска добавляем обработчик ввода текста
        searchInput.addTextChangedListener(searchInputTextWatcher)

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                showContent(Content.SEARCH_RESULT)
            }
        }

        // при запуске скрываем или показываем кнопку очистки формы
        searchClearInputButton.visibility = clearButtonVisibility(searchInputQuery)

        // ставим фокус на форму поиска
        searchInput.requestFocus()

        // настраиваем адаптер для поиска
        rvSearchResults.adapter = searchAdapter
        // если нажата кнопка done на клавиатуре - ищем
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }
        // нажатие кнопки Обновить на экране с ошибкой повторяет поиск
        errorButton.setOnClickListener {
            search()
        }

        // настраиваем адаптер для истории треков
        rvTracksHistory.adapter = historyAdapter
        tracksHistory = TracksHistory(getSharedPreferences(PLAYLIST_MAKER_PREFERENCE, MODE_PRIVATE))
        // если поле поиска пустое - показываем историю треков
        if (searchInput.text.isEmpty()) {
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
            api.search(searchInputQuery).enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    when (response.code()) {
                        // сервер ответил успехом
                        200 -> {
                            // результаты поиска не пустые
                            if (response.body()?.results?.isNotEmpty() == true) {
                                searchAdapter.tracks = response.body()?.results!!
                                showContent(Content.SEARCH_RESULT)
                            } else {
                                // ничего не найдено, показываем соответствующий плейсхолдер
                                showContent(Content.NOT_FOUND)
                            }
                        }
                        // сервер вернул ошибку - показываем соответствующий плейсхолдер
                        else -> {
                            showContent(Content.ERROR)
                        }
                    }
                }

                // ошибка сети, показываем соответствующий плейсхолдер
                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showContent(Content.ERROR)
                }
            })
        }
    }

    // показывает нужный контент
    private fun showContent(content: Content) {
        when (content) {
            Content.NOT_FOUND -> {
                rvSearchResults.visibility = View.GONE
                placeholderError.visibility = View.GONE
                youSearched.visibility = View.GONE
                progressBar.visibility = View.GONE
                placeholderNotFound.visibility = View.VISIBLE
            }

            Content.ERROR -> {
                rvSearchResults.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                youSearched.visibility = View.GONE
                progressBar.visibility = View.GONE
                placeholderError.visibility = View.VISIBLE
            }

            Content.TRACKS_HISTORY -> {
                rvSearchResults.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.GONE
                progressBar.visibility = View.GONE
                youSearched.visibility = View.VISIBLE
            }

            Content.SEARCH_RESULT -> {
                youSearched.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.GONE
                progressBar.visibility = View.GONE
                rvSearchResults.visibility = View.VISIBLE
            }

            Content.PROGRESS_BAR -> {
                youSearched.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.GONE
                rvSearchResults.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
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
        searchInput.setText("")
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
            searchInput.setText(searchInputQuery)
            search()
        }
    }

}