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
    // можно обойтись и без этой переменной, но в ТЗ она требуется
    private var searchQwery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val clearButton = findViewById<ImageView>(R.id.clear_search_form)
        val editText = findViewById<EditText>(R.id.input_search_form)

        // закрываем SearchActivity и возвращаемся на предыдущее
        findViewById<ImageView>(R.id.back_to_main_activity).setOnClickListener {
            finish()
        }

        // очищаем форму поиска
        clearButton.setOnClickListener {
            editText.setText("")

            // Прячем клавиатуру, это есть в ТЗ
            // У кнопки «Очистить» есть OnClickListener, в котором очищается поисковый запрос и прячется клавиатура
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        // при запуске скрываем или показываем кнопку очистки формы и устанавливаем фокус в поле поиска
        clearButton.visibility = clearButtonVisibility(editText.text)
        editText.requestFocus()

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // при изменении текста скрываем или показываем кнопку очистки формы
                clearButton.visibility = clearButtonVisibility(s)
                // сохраняем тест в переменную
                searchQwery = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        editText.addTextChangedListener(simpleTextWatcher)

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
        outState.putString(SEARCH_QUERY, searchQwery)
    }

    // Можно обойтись без onRestoreInstanceState, потому что в методе onCreate тоже передаётся Bundle
    // Но в ТЗ указано:
    // "Переопределён метод onRestoreInstantState().
    // В этом методе в переменную для хранения текста поискового запроса помещается сохранённый текст.
    // Восстановленный текст помещается в EditText."
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQwery = savedInstanceState.getString(SEARCH_QUERY,"")
        findViewById<EditText>(R.id.input_search_form).setText(searchQwery)
    }

}