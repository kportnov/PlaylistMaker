package com.bignerdranch.android.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.button_search)
        val buttonMediaLibrary = findViewById<Button>(R.id.button_media_library)
        val buttonSettings = findViewById<Button>(R.id.button_settings)

        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        buttonSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        buttonMediaLibrary.setOnClickListener {
            val intent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(intent)
        }


/*
        //Домашнее задание с тостами>>>

        val buttonListener = object : OnClickListener {
            override fun onClick(p0: View?) {
                showToast(true, p0)
            }
        }

        //через анонимные классы объеденил в один слушатель
        buttonSearch.setOnClickListener(buttonListener)
        buttonMediaLibrary.setOnClickListener(buttonListener)
        buttonSettings.setOnClickListener(buttonListener)


        //через лямбда
        buttonSearch.setOnClickListener { showToast(false, it) }
        buttonMediaLibrary.setOnClickListener { showToast(false, it) }
        buttonSettings.setOnClickListener { showToast(false, it) }*/

    }

/*    fun showToast(isAnonymous: Boolean, p0: View?) {
        val searchText1 = "Button \"Search\" has been clicked and anonymous class works"
        val searchText2 = "Button \"Search\" has been clicked and lyambda works"
        val mediaText1 = "Button \"Media room\" has been clicked and anonymous class works"
        val mediaText2 = "Button \"Media room\" has been clicked and lyambda works"
        val settingsText1 = "Button \"Settings\" has been clicked and anonymous class works"
        val settingsText2 = "Button \"Settings\" has been clicked and lyambda works"
        val text = when (p0?.id) {
            R.id.button_search -> if (isAnonymous) searchText1 else searchText2
            R.id.button_media_library -> if (isAnonymous) mediaText1 else mediaText2
            R.id.button_settings -> if (isAnonymous) settingsText1 else settingsText2
            else -> "ERROR"
        }
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
    }*/
}