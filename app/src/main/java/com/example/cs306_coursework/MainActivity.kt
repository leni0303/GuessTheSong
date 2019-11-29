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

        //retrieve a pref
        val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)

        val mode = sharedpreferences.getString("com.example.myapp.MODE", null)
        val song = sharedpreferences.getString("com.example.myapp.SONG", null)
        Log.d("TAG", mode)
        Log.d("TAG", song)
    }

    // read a specific song from specific mode from assets
    private fun readSongLyrics(mode: String, fileName: String): String {
        return application.assets.open("$mode/$fileName").bufferedReader().use { it.readText()}
    }

    private fun readSongLyricsAsList(mode: String, fileName: String): List<String> {
        return application.assets.open("$mode/$fileName").bufferedReader().readLines()
    }

    fun selectRandSong(mode: String): String {
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
        Toast.makeText(this@MainActivity, "Change Mode", Toast.LENGTH_LONG)
            .show()
    }
    fun clickChangeSong(item: MenuItem) {
        Toast.makeText(this@MainActivity, "Chaneg Song", Toast.LENGTH_LONG)
            .show()
    }
    fun clickSongList(item: MenuItem) {
        Toast.makeText(this@MainActivity, "Song List", Toast.LENGTH_LONG)
            .show()
    }
    fun clickQuitApp(item: MenuItem) {
        Toast.makeText(this@MainActivity, "Quit App", Toast.LENGTH_LONG)
            .show()
    }
}
