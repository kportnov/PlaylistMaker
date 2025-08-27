package com.bignerdranch.android.playlistmaker

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {

    private var editTextValue = EDIT_TEXT_INPUT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val editTextSearch = findViewById<EditText>(R.id.edit_text_search)
        val imageViewClear = findViewById<ImageView>(R.id.clearIcon)
        val buttonBack = findViewById<Button>(R.id.button_back)

        val textWatcherSearch = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                imageViewClear.visibility = clearButtonVisibility(p0)
                editTextValue = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }

        editTextSearch.addTextChangedListener(textWatcherSearch)
        editTextSearch.setText(editTextValue)

        imageViewClear.setOnClickListener {
            editTextSearch.text.clear()
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putString(EDIT_TEXT, editTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(EDIT_TEXT, EDIT_TEXT_INPUT)
    }

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val EDIT_TEXT_INPUT = ""
    }
}