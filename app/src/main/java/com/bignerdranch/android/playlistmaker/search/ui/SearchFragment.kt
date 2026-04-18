package com.bignerdranch.android.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentSearchBinding
import com.bignerdranch.android.playlistmaker.main.ui.MainActivity
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState
import com.bignerdranch.android.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue
import kotlin.toString

class SearchFragment: Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel {
        parametersOf(context)
    }

    private lateinit var adapter: SearchTrackAdapter
    private lateinit var adapterHistory: SearchTrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        adapter = SearchTrackAdapter { track ->
            (activity as MainActivity).animateBottomNavigationView()
            onTrackClickDebounce(track)
        }

        adapterHistory = SearchTrackAdapter { track ->
            (activity as MainActivity).animateBottomNavigationView()
            onTrackClickDebounce(track)
        }

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            viewModel.addToHistory(track)
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment)
        }

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
                viewModel.getHistory()
            } else {
                viewModel.searchDebounce(
                    changedText = text.toString()
                )
            }
        }

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.editTextSearch.text.isEmpty()) {
                binding.clearIcon.visibility = View.GONE
                viewModel.getHistory()
            }
        }

        binding.error.btnUpdate.setOnClickListener {
            viewModel.searchRequest(binding.editTextSearch.text.toString())
        }

        binding.clearIcon.setOnClickListener {
            binding.editTextSearch.text.clear()
            binding.clearIcon.visibility = View.GONE
            viewModel.getHistory()
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.history.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }


    override fun onResume() {
        super.onResume()
        if (viewModel.observeState().value !is SearchState.Content) {
            viewModel.getHistory()
        }
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

    private fun showConnectionError(messageId: Int) {
        binding.apply {
            recyclerSearch.visibility = View.GONE
            history.viewGroupSearchHistory.visibility = View.GONE
            progressBar.visibility = View.GONE
            error.viewGroupError.visibility = View.VISIBLE
            error.textViewError.text = getString(messageId)
            error.imageViewError.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.img_connection_failure
                )
            )
        }
    }

    private fun showEmpty(messageId: Int) {
        binding.apply {
            recyclerSearch.visibility = View.GONE
            progressBar.visibility = View.GONE
            history.viewGroupSearchHistory.visibility = View.GONE
            error.viewGroupError.visibility = View.VISIBLE
            error.btnUpdate.visibility = View.GONE
            error.textViewError.text = getString(messageId)
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
            is SearchState.Error -> showConnectionError(state.errorMessageId)
            is SearchState.Empty -> showEmpty(state.messageId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
    
}