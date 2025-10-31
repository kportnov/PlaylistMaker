package com.bignerdranch.android.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SHARED_PREFERENCES_SEARCH= "preferences_search"
const val SEARCH_HISTORY_KEY = "key_for_history_key"

class SearchActivity : AppCompatActivity() {

    private var editTextValue = EDIT_TEXT_INPUT
    //var state for keeping visibility condition of ERROR elements after day/dark mode change
    private var state = STATE

    private val iTunesSearchAPIBaseURL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesSearchAPIBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesSearchAPI::class.java)

    private val tracks = mutableListOf<Track>()
    private val adapter = TrackAdapter(tracks, this)

    private val trackHistory = mutableListOf<Track>()
    private val trackHistoryAdapter = TrackAdapter(trackHistory, this)

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

    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener

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
        val searchHistory = SearchHistory(sharedPreferences)
        trackHistory.addAll(searchHistory.readSharedPreferences())

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val imageViewClear = findViewById<ImageView>(R.id.clearIcon)
        buttonBack = findViewById(R.id.button_back)
        editTextSearch = findViewById(R.id.edit_text_search)

        viewGroupError = findViewById(R.id.viewGroupError)
        viewGroupSearchHistory = findViewById(R.id.viewGroupSearchHistory)
        imageError = findViewById(R.id.imageViewError)
        textViewError = findViewById(R.id.textViewError)
        buttonUpdate = findViewById(R.id.btn_update)
        buttonClearHistory = findViewById(R.id.btn_clear_history)

        recyclerView = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        recyclerViewHistory = findViewById(R.id.recycler_search_history)
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = trackHistoryAdapter


        sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SEARCH_HISTORY_KEY) {
                trackHistory.clear()
                trackHistory.addAll(searchHistory.readSharedPreferences())
                trackHistoryAdapter.notifyDataSetChanged()
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        layoutHandler.manageLayout()

        editTextSearch.doOnTextChanged { text, _, _, _ ->
            imageViewClear.isVisible = !text.isNullOrEmpty()
            editTextValue = text.toString()

            if (editTextSearch.hasFocus() && text?.isEmpty() == true) {
                layoutHandler.setLayout(ViewGroupAdditional.SEARCH_HISTORY)
            } else {
                if (!layoutHandler.isError()) {
                    layoutHandler.setLayout(ViewGroupAdditional.NORMAL)
                }
            }
        }

        editTextSearch.setText(editTextValue)

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(false)
                true
            }
            false
        }

        editTextSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && editTextSearch.text.isEmpty()) {
                layoutHandler.setLayout(ViewGroupAdditional.SEARCH_HISTORY)
            } else {
                if (!layoutHandler.isError()) {
                    layoutHandler.setLayout(ViewGroupAdditional.NORMAL)
                }
            }
        }

        buttonUpdate.setOnClickListener {
            search(true)
        }

        imageViewClear.setOnClickListener {
            editTextSearch.text.clear()
            tracks.clear()
            adapter.notifyDataSetChanged()
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            layoutHandler.setLayout(ViewGroupAdditional.SEARCH_HISTORY)
        }

        buttonClearHistory.setOnClickListener {
            searchHistory.clearHistoryList()
            layoutHandler.setLayout(ViewGroupAdditional.NORMAL)
        }

        buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun search(updateClicked: Boolean) {
        //when update btn has been clicked search last request, NOT current edit_text (according task)
        val request = if (updateClicked) {
            lastRequest
        } else {
            lastRequest = editTextSearch.text.toString()
            lastRequest
        }
        if (request.isNotEmpty()) {
            iTunesService.search(request)
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            if (response.body()?.tracks!!.isNotEmpty()) {
                                tracks.clear()
                                tracks.addAll(response.body()?.tracks!!)
                                adapter.notifyDataSetChanged()
                                layoutHandler.setLayout(ViewGroupAdditional.NORMAL)
                            } else {
                                layoutHandler.setLayout(ViewGroupAdditional.NOTHING_FOUND)
                            }
                        } else {
                            layoutHandler.setLayout(ViewGroupAdditional.CONNECTION_FAILURE)
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        layoutHandler.setLayout(ViewGroupAdditional.CONNECTION_FAILURE)
                    }

                })
        }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, editTextValue)
        outState.putInt(STATE_CONDITION, state)
    }



    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val EDIT_TEXT_INPUT = ""
        const val STATE = 0
        const val STATE_CONDITION = "STATE"
    }

    private enum class ViewGroupAdditional {
        NORMAL,
        SEARCH_HISTORY,
        CONNECTION_FAILURE,
        NOTHING_FOUND,
    }

    private inner class ViewGroupHandler {

        fun setLayout(viewGroup: ViewGroupAdditional) {

            state = when (viewGroup) {
                ViewGroupAdditional.NORMAL -> 0
                ViewGroupAdditional.SEARCH_HISTORY -> 1
                ViewGroupAdditional.NOTHING_FOUND -> 2
                ViewGroupAdditional.CONNECTION_FAILURE -> 3
            }
            manageLayout()
        }

        private fun getViewGroupByState(): ViewGroupAdditional {
            return when (state) {
                0 -> ViewGroupAdditional.NORMAL
                1 -> ViewGroupAdditional.SEARCH_HISTORY
                2 -> ViewGroupAdditional.NOTHING_FOUND
                else -> ViewGroupAdditional.CONNECTION_FAILURE
            }
        }


        fun manageLayout() {

            when (getViewGroupByState()) {
                ViewGroupAdditional.NORMAL -> {
                    viewGroupError.visibility = View.GONE
                    viewGroupSearchHistory.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                ViewGroupAdditional.SEARCH_HISTORY -> {
                    if (trackHistory.isNotEmpty()) {
                        viewGroupError.visibility = View.GONE
                        viewGroupSearchHistory.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }

                ViewGroupAdditional.NOTHING_FOUND -> {
                    viewGroupError.visibility = View.VISIBLE
                    viewGroupSearchHistory.visibility = View.GONE
                    recyclerView.visibility = View.GONE

                    buttonUpdate.visibility = View.GONE

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

                    textViewError.text = resources.getText(R.string.connection_problems)
                    imageError.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@SearchActivity,
                            R.drawable.img_connection_failure
                        )
                    )
                }
            }
        }

        fun isError(): Boolean {
            return getViewGroupByState() == ViewGroupAdditional.CONNECTION_FAILURE ||
                    getViewGroupByState() == ViewGroupAdditional.CONNECTION_FAILURE
        }
    }
}