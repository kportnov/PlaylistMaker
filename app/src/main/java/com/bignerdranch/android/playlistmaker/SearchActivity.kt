package com.bignerdranch.android.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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
    private val adapter = TrackAdapter(tracks)

    private lateinit var editTextSearch: EditText
    private lateinit var buttonBack: Button

    private lateinit var recyclerView: RecyclerView

    private lateinit var imageError: ImageView
    private lateinit var textViewError: TextView
    private lateinit var buttonUpdate: Button

    private lateinit var lastRequest: String

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

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val imageViewClear = findViewById<ImageView>(R.id.clearIcon)
        buttonBack = findViewById(R.id.button_back)
        editTextSearch = findViewById(R.id.edit_text_search)

        imageError = findViewById(R.id.imageViewError)
        textViewError = findViewById(R.id.textViewError)
        buttonUpdate = findViewById(R.id.btn_update)

        recyclerView = findViewById(R.id.recycler_search)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        manageErrorElements(getErrorTypeBySTATE())

        editTextSearch.doOnTextChanged { text, _, _, _ ->
            imageViewClear.isVisible = !text.isNullOrEmpty()
            editTextValue = text.toString()
        }

        editTextSearch.setText(editTextValue)

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(false)
                true
            }
            false
        }

        buttonUpdate.setOnClickListener { search(true) }

        imageViewClear.setOnClickListener {
            editTextSearch.text.clear()
            tracks.clear()
            adapter.notifyDataSetChanged()
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            manageErrorElements(ErrorType.NO_ERROR)
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
                                manageErrorElements(ErrorType.NO_ERROR)
                            } else {
                                manageErrorElements(ErrorType.NOTHING_FOUND)
                            }
                        } else {
                            manageErrorElements(ErrorType.CONNECTION_FAILURE)
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        manageErrorElements(ErrorType.CONNECTION_FAILURE)
                    }

                })
        }
    }

    private fun getErrorTypeBySTATE(): ErrorType {
        return when (state) {
            1 -> ErrorType.NOTHING_FOUND
            2 -> ErrorType.CONNECTION_FAILURE
            else -> ErrorType.NO_ERROR
        }
    }

    private fun manageErrorElements(errorType: ErrorType) {
        when (errorType) {
            ErrorType.NO_ERROR -> {
                state = 0
                imageError.visibility = View.GONE
                textViewError.visibility = View.GONE
                buttonUpdate.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            ErrorType.NOTHING_FOUND -> {
                state = 1
                imageError.visibility = View.VISIBLE
                textViewError.visibility = View.VISIBLE
                buttonUpdate.visibility = View.GONE
                recyclerView.visibility = View.GONE
                textViewError.text = resources.getText(R.string.nothing_found)
                imageError.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.img_nothing_found))
            }
            ErrorType.CONNECTION_FAILURE -> {
                state = 2
                imageError.visibility = View.VISIBLE
                textViewError.visibility = View.VISIBLE
                buttonUpdate.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                textViewError.text = resources.getText(R.string.connection_problems)
                imageError.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.img_connection_failure))
            }
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

    private enum class ErrorType {
        CONNECTION_FAILURE,
        NOTHING_FOUND,
        NO_ERROR
    }
}