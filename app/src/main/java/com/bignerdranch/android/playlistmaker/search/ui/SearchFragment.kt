package com.bignerdranch.android.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
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
import com.bignerdranch.android.playlistmaker.util.ConnectivityChangedReceiver
import com.bignerdranch.android.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue
import kotlin.toString

class SearchFragment: Fragment() {
    private val viewModel: SearchViewModel by viewModel {
        parametersOf(context)
    }

    private val connectivityChangedReceiver = ConnectivityChangedReceiver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {

            setContent {

                val state by viewModel.observeState().observeAsState(SearchState.Loading)

                var searchText by remember { mutableStateOf("") }

                SearchScreen(
                    state = state,

                    searchText = searchText,

                    onSearchTextChanged = {

                        searchText = it

                        if (searchText.isEmpty()) {
                            viewModel.getHistory()
                        } else {
                            viewModel.searchDebounce(searchText)
                        }
                    },

                    onTrackClick = { track ->

                        viewModel.addToHistory(track)

                        findNavController().navigate(
                            R.id.action_searchFragment_to_playerFragment
                        )
                    },

                    onClearHistoryClick = {
                        viewModel.clearHistory()
                    },

                    onUpdateClick = {
                        viewModel.searchRequest(searchText)
                    }


                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (viewModel.observeState().value !is SearchState.Content) {
            viewModel.getHistory()
        }

        ContextCompat.registerReceiver(
            requireContext(),
            connectivityChangedReceiver,
            IntentFilter(ConnectivityChangedReceiver.ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(connectivityChangedReceiver)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
    
}