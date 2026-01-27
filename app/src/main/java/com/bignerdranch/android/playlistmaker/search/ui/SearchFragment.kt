package com.bignerdranch.android.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentSearchBinding
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue
import kotlin.text.clear
import kotlin.toString

class SearchFragment: Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModel {
        parametersOf(context)
    }

    private val adapter = TrackAdapter { adapterInit(it) }
    private val adapterHistory = TrackAdapter { adapterInit(it) }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.recyclerSearch.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerSearch.adapter = adapter
        binding.history.recyclerSearchHistory.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.history.recyclerSearchHistory.adapter = adapterHistory


        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
            binding.clearIcon.isVisible = !text.isNullOrEmpty()
            if (binding.editTextSearch.hasFocus() && text?.isEmpty() == true) {
                viewModel.loadHistory()
            } else {
                viewModel.searchDebounce(
                    changedText = text.toString()
                )
            }
        }

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.editTextSearch.text.isEmpty()) {
                binding.clearIcon.visibility = View.GONE
                viewModel.loadHistory()
            }
        }

        binding.error.btnUpdate.setOnClickListener {
            viewModel.searchRequest(binding.editTextSearch.text.toString())
        }

        binding.clearIcon.setOnClickListener {
            binding.editTextSearch.text.clear()
            binding.clearIcon.visibility = View.GONE
            viewModel.loadHistory()
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.history.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }


    override fun onResume() {
        super.onResume()
        if (viewModel.observeState().value !is SearchState.Content) {
            viewModel.loadHistory()
        }
    }


    private fun adapterInit(track: Track) {
        if (clickDebounce()) {
            viewModel.addToHistory(track)
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment)
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showLoading() {
        binding.apply {
            recyclerSearch.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            history.viewGroupSearchHistory.visibility = View.GONE
            error.viewGroupError.visibility = View.GONE
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.apply {
            recyclerSearch.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            history.viewGroupSearchHistory.visibility = View.GONE
            error.viewGroupError.visibility = View.GONE

        }
        adapter.trackList.clear()
        adapter.trackList.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun showHistory(tracks: List<Track>) {
        binding.apply {
            recyclerSearch.visibility = View.GONE
            progressBar.visibility = View.GONE
            history.viewGroupSearchHistory.visibility = View.VISIBLE
            history.recyclerSearchHistory.visibility = View.VISIBLE
            error.viewGroupError.visibility = View.GONE
        }
        adapterHistory.trackList.clear()
        adapterHistory.trackList.addAll(tracks)
        adapterHistory.notifyDataSetChanged()
    }

    private fun showConnectionError(message: String) {
        binding.apply {
            recyclerSearch.visibility = View.GONE
            history.viewGroupSearchHistory.visibility = View.GONE
            progressBar.visibility = View.GONE
            error.viewGroupError.visibility = View.VISIBLE
            error.textViewError.text = message
            error.imageViewError.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.img_connection_failure
                )
            )
        }
    }

    private fun showEmpty(message: String) {
        binding.apply {
            recyclerSearch.visibility = View.GONE
            progressBar.visibility = View.GONE
            history.viewGroupSearchHistory.visibility = View.GONE
            error.viewGroupError.visibility = View.VISIBLE
            error.btnUpdate.visibility = View.GONE
            error.textViewError.text = message
            error.imageViewError.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.img_nothing_found
                )
            )
        }
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.History -> showHistory(state.tracks)
            is SearchState.Error -> showConnectionError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    
}