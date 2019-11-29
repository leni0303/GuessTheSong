package com.example.cs306_coursework

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class FirstTimeUserActivity : AppCompatActivity() {
    private val sharedPreferenceName = "pref"
    //put in @string
    private val sharedPreferencesKey = "com.example.myapp.MODE"
    private val sharedPreferencesKeySong = "com.example.myapp.SONG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_user)

    }

    private fun selectRandSong(mode: String): String {
        return application.assets.list(mode).random()
    }

    private fun savePrefMode(mode: String) {
        val sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()

        editor.putString(sharedPreferencesKey, mode)
        editor.commit()
    }

    private fun savePrefRandSong(song: String) {
        val sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()

        editor.putString(sharedPreferencesKeySong, song)
        editor.commit()
    }

    fun chooseClassic(view: View) {
        val  randSong = selectRandSong("classic")

        savePrefRandSong(randSong)
        savePrefMode("classic")

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    fun chooseCurrent(view: View) {
        val randSong = selectRandSong("current")

        savePrefRandSong(randSong)
        savePrefMode("current")

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}
