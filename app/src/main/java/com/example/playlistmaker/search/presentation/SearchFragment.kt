package com.example.playlistmaker.search.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.TRACK
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.common.models.Track
import com.example.playlistmaker.common.presentation.TracksAdapter
import com.example.playlistmaker.search.presentation.models.SearchState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val searchViewModel by viewModel<SearchViewModel>()

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val searchAdapter = TracksAdapter({clickOnTrack(it)})

    private val historyAdapter = TracksAdapter({clickOnTrack(it)})

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
        searchViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        // подписываемся на тосты
        searchViewModel.observeShowToast().observe(viewLifecycleOwner) {
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
            searchViewModel.searchDebounce(binding.inputSearchForm.text.toString())
        }

        // если нажата кнопка done на клавиатуре - ищем
        binding.inputSearchForm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchViewModel.search(binding.inputSearchForm.text.toString())
            }
            false
        }

        // нажатие кнопки Обновить на экране с ошибкой повторяет поиск
        binding.errorButton.setOnClickListener {
            searchViewModel.search(binding.inputSearchForm.text.toString())
        }

        // по клику на кнопке очистки истории поиска - очищаем историю поиска
        binding.clearHistoryButton.setOnClickListener {
            confirmDialog.show()
        }

        // при запуске скрываем или показываем кнопку очистки формы
        binding.clearSearchFormButton.visibility =
            clearButtonVisibility(binding.inputSearchForm.text)

        // ставим фокус на форму поиска
        binding.inputSearchForm.requestFocus()

        confirmDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.clear_history_q))
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            setPositiveButton(getString(R.string.clear)) { _, _ ->
                searchViewModel.clearTracksHistory(getString(R.string.history_was_clear))
            }
        }
    }

    private fun clearSearch() {
        searchAdapter.tracks = listOf()
        binding.inputSearchForm.setText("")
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        searchViewModel.clearSearch()
    }

    private fun clickOnTrack(track: Track) {
        if (searchViewModel.clickDebounce()) {
            searchViewModel.addTracksHistory(track)
            findNavController().navigate(
                R.id.action_to_PlayerFragment,
                Bundle().apply {
                    putSerializable(TRACK, track)
                }
            )
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