package com.example.cs306_coursework

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.util.Log

import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomappbar.BottomAppBar

class MainActivity : AppCompatActivity() {
    private val sharedPreference = "pref"
    private val sharedPreferenceKey = "isFirstTimeUser"
    var songLyricsList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check if it's a first time user
        checkFirstTimeUser()

        // handle menu options for bottom app bar
        val bar = findViewById<BottomAppBar>(R.id.bar)
        bar.setNavigationOnClickListener {
            // show menu items
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
        }

        //get song and put lyrics in a list

    }

    // read a specific song from specific mode from assets
    private fun readSongLyrics(mode: String, fileName: String): String {
        return application.assets.open("$mode/$fileName").bufferedReader().use { it.readText()}
    }

    private fun readSongLyricsAsList(mode: String, fileName: String): List<String> {
        return application.assets.open("$mode/$fileName").bufferedReader().readLines()
    }

    fun SaveSongInList() {
        val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val currentMode = sharedpreferences.getString("com.example.myapp.MODE", null)
        val currentSong = sharedpreferences.getString("com.example.myapp.SONG", null)

        songLyricsList = readSongLyricsAsList(currentMode, currentSong)
    }

    private fun selectRandSong(mode: String): String {
        return application.assets.list(mode).random()
    }

    // check if user opens the app for the first time
    private fun checkFirstTimeUser() {

        val isFirstRun = getSharedPreferences(sharedPreference, Context.MODE_PRIVATE)
            .getBoolean(sharedPreferenceKey, true)

        if (isFirstRun) {
            startActivity(Intent(this@MainActivity, FirstTimeUserActivity::class.java))
            Toast.makeText(this@MainActivity, "Run only once", Toast.LENGTH_LONG)
                .show()
        }

        getSharedPreferences(sharedPreference, Context.MODE_PRIVATE).edit()
            .putBoolean(sharedPreferenceKey, false).apply()
    }

    /*--------------------Handle Btn Clicks--------------------*/
    // button action for bottom app items
    fun fabBtnClick(view: View) {
        Toast.makeText(this@MainActivity, "FAB  btn click", Toast.LENGTH_LONG)
            .show()
    }

    fun showFavSongs(item: MenuItem) {
        Toast.makeText(this@MainActivity, "Fav btn click", Toast.LENGTH_LONG)
            .show()
    }

    // button action for menu items
    fun clickTopSongs(item: MenuItem) {
        Toast.makeText(this@MainActivity, "Top Songs", Toast.LENGTH_LONG)
            .show()
    }

    fun clickChangeMode(item: MenuItem) {
        val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val currentMode = sharedpreferences.getString("com.example.myapp.MODE", null)
        val editor = sharedpreferences.edit()

        if (currentMode == "classic") {
            editor.putString("com.example.myapp.MODE", "current")
            //TODO: Choose new song or resume with previous
        } else {
            //TODO: Choose new song or resume with previous
            editor.putString("com.example.myapp.MODE", "classic")
        }
        editor.commit()
    }

    fun clickChangeSong(item: MenuItem) {
        val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val currentMode = sharedpreferences.getString("com.example.myapp.MODE", null)
        val currentSong = sharedpreferences.getString("com.example.myapp.SONG", null)
        val editor = sharedpreferences.edit()

        var randomSong = selectRandSong(currentMode)
        while(randomSong == currentSong) {
            randomSong = selectRandSong(currentMode)
        }

        editor.putString("com.example.myapp.SONG", randomSong)
        editor.commit()

        SaveSongInList()
    }
    fun clickSongList(item: MenuItem) {
        Toast.makeText(this@MainActivity, "Song List", Toast.LENGTH_LONG)
            .show()
    }
    fun clickQuitApp(item: MenuItem) {
        finishAndRemoveTask()
    }
}
