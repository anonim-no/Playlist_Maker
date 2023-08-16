package com.example.playlistmaker.search.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.common.TRACK
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.presentation.PlayerActivity
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.common.presentation.TracksAdapter
import com.example.playlistmaker.search.presentation.models.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // подписываемся на изменения состояния
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        // подписываемся на тосты
        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        // адаптер для поиска
        binding.rvSearchResults.adapter = searchAdapter
        // адаптер для истории треков
        binding.rvTracksHistory.adapter = historyAdapter

        //  по клику на крестике очищаем форму и результаты поиска
        binding.clearSearchFormButton.setOnClickListener {
            clearSearch()
        }

        // к форме поиска добавляем обработчик ввода текста
        binding.inputSearchForm.doOnTextChanged { s: CharSequence?, _, _, _ ->
            // при изменении текста скрываем или показываем кнопку очистки формы
            binding.clearSearchFormButton.visibility = clearButtonVisibility(s)
            // если начали заполнять поле ввода - скрываем историю треков
            if (
                binding.inputSearchForm.hasFocus()
                && s.toString().isNotEmpty()
                && binding.youSearched.visibility == View.VISIBLE
            ) {
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
        searchAdapter.tracks = listOf()
        binding.inputSearchForm.setText("")
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        viewModel.clearSearch()
    }

    private fun clickOnTrack(track: Track) {
        if (viewModel.clickDebounce()) {
            viewModel.addTracksHistory(track)
            val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra(TRACK, track)
            }
            startActivity(intent)
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
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
                when (state.errorCode) {
                    -1 -> binding.errorText.text =
                        resources.getText(R.string.check_internet_connection)

                    else -> binding.errorText.text =
                        String.format(resources.getText(R.string.error).toString(), state.errorCode)
                }

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