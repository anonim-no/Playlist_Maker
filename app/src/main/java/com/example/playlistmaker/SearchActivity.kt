package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
    }

    // в переменную сохраняется текст из поисковой строки при изменении
    private var searchInputQwery = ""

    private lateinit var searchInput: EditText
    private lateinit var searchInputClearButton: ImageView
    private val searchInputTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // при изменении текста скрываем или показываем кнопку очистки формы
            searchInputClearButton.visibility = clearButtonVisibility(s)
            // сохраняем тест в переменную
            searchInputQwery = s.toString()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // закрываем SearchActivity и возвращаемся на предыдущее
        findViewById<ImageView>(R.id.back_to_main_activity).setOnClickListener {
            finish()
        }

        searchInput = findViewById(R.id.input_search_form)
        // ставим фокус на форму поиска
        searchInput.requestFocus()
        // к форме поиска добавляем обработчик ввода текста
        searchInput.addTextChangedListener(searchInputTextWatcher)

        searchInputClearButton = findViewById(R.id.clear_search_form)
        // при запуске скрываем или показываем кнопку очистки формы
        searchInputClearButton.visibility = clearButtonVisibility(searchInput.text)
        // очищаем форму поиска
        searchInputClearButton.setOnClickListener {
            clearSearchForm()
        }
    }

    private fun clearSearchForm() {
        searchInput.setText("")

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
        outState.putString(SEARCH_QUERY, searchInputQwery)
    }

    // восстанавливаем текст и помещаем в EditText
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInputQwery = savedInstanceState.getString(SEARCH_QUERY,"")
        searchInput.setText(searchInputQwery)
    }

}