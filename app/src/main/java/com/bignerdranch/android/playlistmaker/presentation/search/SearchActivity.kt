package com.bignerdranch.android.playlistmaker.presentation.search

import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.playlistmaker.Creator
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.data.TrackHistoryManager
import com.bignerdranch.android.playlistmaker.presentation.TrackAdapter
import com.bignerdranch.android.playlistmaker.domain.api.TracksHistoryInteractor
import com.bignerdranch.android.playlistmaker.domain.api.TracksInteractor
import com.bignerdranch.android.playlistmaker.domain.models.Track
import com.bignerdranch.android.playlistmaker.presentation.player.KEY_PLAYER_ACTIVITY
import com.bignerdranch.android.playlistmaker.presentation.player.PlayerActivity
import com.google.gson.Gson


const val SHARED_PREFERENCES_SEARCH= "preferences_search"
const val SEARCH_HISTORY_KEY = "key_for_history_key"

class SearchActivity : AppCompatActivity() {

    private var editTextValue = EDIT_TEXT_INPUT
    //var state for keeping visibility condition of ERROR elements after day/dark mode change
    private var state = STATE


    private lateinit var tracksHistoryInteractorImpl: TracksHistoryInteractor
    private lateinit var trackHistoryManager: TrackHistoryManager
    private lateinit var tracksInteractorImpl: TracksInteractor


    private lateinit var editTextSearch: EditText
    private lateinit var buttonBack: ImageButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var viewGroupError: LinearLayout
    private lateinit var viewGroupSearchHistory: ConstraintLayout

    private lateinit var imageError: ImageView
    private lateinit var textViewError: TextView
    private lateinit var buttonUpdate: Button

    private lateinit var buttonClearHistory: Button

    private lateinit var lastRequest: String
    private val layoutHandler = ViewGroupHandler()
    private lateinit var progressBar: ProgressBar

    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val searchRunnable = Runnable { loadTracks(false) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)


        if (savedInstanceState != null) {
            editTextValue = savedInstanceState.getString(EDIT_TEXT, EDIT_TEXT_INPUT)
            state = savedInstanceState.getInt(STATE_CONDITION, STATE)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_SEARCH, MODE_PRIVATE)

        trackHistoryManager = Creator.getTrackHistoryManager(this)
        tracksHistoryInteractorImpl = Creator.getTracksHistoryInteractor(trackHistoryManager)
        tracksInteractorImpl = Creator.provideTracksInteractor()


        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        val imageViewClear = findViewById<ImageView>(R.id.clearIcon)
        buttonBack = findViewById(R.id.button_back)
        editTextSearch = findViewById(R.id.edit_text_search)
        viewGroupError = findViewById(R.id.viewGroupError)
        viewGroupSearchHistory = findViewById(R.id.viewGroupSearchHistory)
        imageError = findViewById(R.id.imageViewError)
        textViewError = findViewById(R.id.textViewError)
        buttonUpdate = findViewById(R.id.btn_update)
        buttonClearHistory = findViewById(R.id.btn_clear_history)
        progressBar = findViewById(R.id.progressBar)

        recyclerView = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerViewHistory = findViewById(R.id.recycler_search_history)
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)

        sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SEARCH_HISTORY_KEY) {
                loadHistory()
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchDebounce()
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                imageViewClear.isVisible = !text.isNullOrEmpty()
                editTextValue = text.toString()

                if (editTextSearch.hasFocus() && text?.isEmpty() == true) {
                    loadHistory()
                } else {
                    if (!layoutHandler.isError()) {
                        layoutHandler.setLayout(ViewGroupAdditional.NORMAL)
                    }
                }
            }

            override fun afterTextChanged(text: Editable?) {
            }
        })

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadTracks(false)
                true
            }
            false
        }

        editTextSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && editTextSearch.text.isEmpty()) {
                loadHistory()
            } else {
                if (!layoutHandler.isError()) {
                    loadTracks(false)
                }
            }
        }

        buttonUpdate.setOnClickListener {
            loadTracks(true)
        }

        imageViewClear.setOnClickListener {
            editTextSearch.text.clear()
            loadHistory()
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        buttonClearHistory.setOnClickListener {
            tracksHistoryInteractorImpl.clearHistory()
        }

        buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        loadTracks(false)
        loadHistory()
    }

    private fun loadTracks(updateClicked: Boolean) {
        val request = if (updateClicked) {
            lastRequest
        } else {
            lastRequest = editTextSearch.text.toString()
            lastRequest
        }
        val adapter = TrackAdapter { adapterInit(it) }
        if (request.isNotEmpty()) {
            layoutHandler.setLayout(ViewGroupAdditional.SEARCH_IN_ACTION)
            tracksInteractorImpl.searchTracks(request, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    adapter.trackList = foundTracks
                    handler.post {
                        recyclerView.adapter = adapter
                        layoutHandler.setLayout(ViewGroupAdditional.NORMAL)
                    }
                }
            })
        } else {
            adapter.trackList = emptyList()
            recyclerView.adapter = adapter
        }
    }

    private fun loadHistory() {
        tracksHistoryInteractorImpl.loadHistory(object : TracksHistoryInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                val adapter = TrackAdapter { adapterInit(it) }
                adapter.trackList = tracks
                recyclerViewHistory.adapter = adapter

                if (tracks.isNotEmpty()) {
                    layoutHandler.setLayout(ViewGroupAdditional.SEARCH_HISTORY)
                } else {
                    layoutHandler.setLayout(ViewGroupAdditional.NORMAL)
                    loadTracks(false)
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, editTextValue)
        outState.putInt(STATE_CONDITION, state)
    }

    private enum class ViewGroupAdditional {
        NORMAL,
        SEARCH_HISTORY,
        CONNECTION_FAILURE,
        NOTHING_FOUND,
        SEARCH_IN_ACTION
    }

    private inner class ViewGroupHandler {

        fun setLayout(viewGroup: ViewGroupAdditional) {

            state = when (viewGroup) {
                ViewGroupAdditional.NORMAL -> 0
                ViewGroupAdditional.SEARCH_HISTORY -> 1
                ViewGroupAdditional.NOTHING_FOUND -> 2
                ViewGroupAdditional.CONNECTION_FAILURE -> 3
                ViewGroupAdditional.SEARCH_IN_ACTION -> 4
            }
            manageLayout()
        }

        private fun getViewGroupByState(): ViewGroupAdditional {
            return when (state) {
                0 -> ViewGroupAdditional.NORMAL
                1 -> ViewGroupAdditional.SEARCH_HISTORY
                2 -> ViewGroupAdditional.NOTHING_FOUND
                3 -> ViewGroupAdditional.CONNECTION_FAILURE
                else -> ViewGroupAdditional.SEARCH_IN_ACTION
            }
        }


        fun manageLayout() {

            when (getViewGroupByState()) {
                ViewGroupAdditional.NORMAL -> {
                    viewGroupError.visibility = View.GONE
                    viewGroupSearchHistory.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }

                ViewGroupAdditional.SEARCH_HISTORY -> {
                    viewGroupError.visibility = View.GONE
                    viewGroupSearchHistory.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }

                ViewGroupAdditional.NOTHING_FOUND -> {
                    viewGroupError.visibility = View.VISIBLE
                    viewGroupSearchHistory.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    buttonUpdate.visibility = View.GONE
                    progressBar.visibility = View.GONE

                    textViewError.text = resources.getText(R.string.nothing_found)
                    imageError.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@SearchActivity,
                            R.drawable.img_nothing_found
                        )
                    )
                }

                ViewGroupAdditional.CONNECTION_FAILURE -> {
                    viewGroupError.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    viewGroupSearchHistory.visibility = View.GONE
                    progressBar.visibility = View.GONE

                    textViewError.text = resources.getText(R.string.connection_problems)
                    imageError.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@SearchActivity,
                            R.drawable.img_connection_failure
                        )
                    )
                }

                ViewGroupAdditional.SEARCH_IN_ACTION -> {
                    viewGroupError.visibility = View.GONE
                    viewGroupSearchHistory.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE

                }
            }
        }

        fun isError(): Boolean {
            return getViewGroupByState() == ViewGroupAdditional.CONNECTION_FAILURE ||
                    getViewGroupByState() == ViewGroupAdditional.CONNECTION_FAILURE
        }
    }

    private fun adapterInit(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(KEY_PLAYER_ACTIVITY, Gson().toJson(track))
            startActivity(intent)
            tracksHistoryInteractorImpl.addToHistory(track)
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

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val EDIT_TEXT_INPUT = ""
        const val STATE = 0
        const val STATE_CONDITION = "STATE"

        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}