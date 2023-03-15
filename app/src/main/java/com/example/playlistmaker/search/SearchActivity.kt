package com.example.playlistmaker.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
    }
    private var searchInputQuery = ""

    enum class PlaceHolder {
        SEARCH_RESULT, NOT_FOUND, ERROR
    }

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(API::class.java)

    private val adapter = SearchAdapter{
        showInfo(it)
    }

    private lateinit var backButton: ImageView
    private lateinit var searchInput: EditText
    private lateinit var searchClearInputButton: ImageView
    private lateinit var rvSearch: RecyclerView
    private lateinit var placeholderNotFound: TextView
    private lateinit var placeholderError: LinearLayout
    private lateinit var errorButton: Button

    private val searchInputTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // при изменении текста скрываем или показываем кнопку очистки формы
            searchClearInputButton.visibility = clearButtonVisibility(s)
            // сохраняем тест в переменную
            searchInputQuery = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.back_to_main_activity)
        searchInput = findViewById(R.id.input_search_form)
        searchClearInputButton = findViewById(R.id.clear_search_form)
        rvSearch = findViewById(R.id.rvSearchResults)
        placeholderNotFound = findViewById(R.id.placeholderNotFound)
        placeholderError = findViewById(R.id.placeholderError)
        errorButton = findViewById(R.id.errorButton)

        // по клику назад закрываем SearchActivity и возвращаемся на предыдущее
        backButton.setOnClickListener {
            finish()
        }

        //  по клику на корестике очищаем форму и результаты поиска
        searchClearInputButton.setOnClickListener {
            clearSearch()
        }

        // к форме поиска добавляем обработчик ввода текста
        searchInput.addTextChangedListener(searchInputTextWatcher)

        // при запуске скрываем или показываем кнопку очистки формы
        searchClearInputButton.visibility = clearButtonVisibility(searchInputQuery)

        // ставим фокус на форму поиска
        searchInput.requestFocus()

        // настраиваем адаптер для поиска
        rvSearch.adapter = adapter
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

    }

    // функция берет строку поиска, делает запрос в апи и показывает результат
    private fun search() {
        if (searchInputQuery.isNotEmpty()) {
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
                                adapter.tracks = response.body()?.results!!
                                showPlaceholder(PlaceHolder.SEARCH_RESULT)
                            } else {
                                // ничего не найдено, показываем соответствующий плейсхолдер
                                showPlaceholder(PlaceHolder.NOT_FOUND)
                            }
                        }
                        // сервер вернул ошибку - показываем соответствующий плейсхолдер
                        else -> {
                            showPlaceholder(PlaceHolder.ERROR)
                        }
                    }
                }
                // ошибка сети, показываем соответствующий плейсхолдер
                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showPlaceholder(PlaceHolder.ERROR)
                }
            })
        }
    }

    // управляем плейсхолдерами
    private fun showPlaceholder(placeholder: PlaceHolder) {
        when (placeholder) {
            PlaceHolder.NOT_FOUND -> {
                adapter.clearTracks()
                placeholderError.visibility = View.GONE
                placeholderNotFound.visibility = View.VISIBLE
            }
            PlaceHolder.ERROR -> {
                adapter.clearTracks()
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.VISIBLE
            }
            else -> {
                placeholderNotFound.visibility = View.GONE
                placeholderError.visibility = View.GONE
            }
        }
    }

    //  показываем информацию по клику на трек в результатах поиска
    private fun showInfo(track: Track) {
        val message = "${track.trackName}\nСтоимость: ${track.trackPrice}$"
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearSearch() {
        // обнуляем поле ввода
        searchInput.setText("")
        // обнуляем список результатов
        adapter.clearTracks()
        // убираем плейсхолдеры, если были
        showPlaceholder(PlaceHolder.SEARCH_RESULT)
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