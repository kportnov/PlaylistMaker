package com.bignerdranch.android.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.ActivitySearchBinding
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.player.ui.PlayerActivity
import com.bignerdranch.android.playlistmaker.search.ui.models.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel {
        parametersOf(applicationContext)
    }

    private val adapter = TrackAdapter { adapterInit(it) }
    private val adapterHistory = TrackAdapter { adapterInit(it) }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.recyclerSearch.layoutManager = LinearLayoutManager(this)
        binding.recyclerSearch.adapter = adapter
        binding.history.recyclerSearchHistory.layoutManager = LinearLayoutManager(this)
        binding.history.recyclerSearchHistory.adapter = adapterHistory


        viewModel.observeState().observe(this) {
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

        binding.buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.observeState().value !is SearchState.Content) {
            viewModel.loadHistory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun adapterInit(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            viewModel.addToHistory(track)
            startActivity(intent)
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
                    this@SearchActivity,
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
                    this@SearchActivity,
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