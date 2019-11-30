package com.example.cs306_coursework

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class FirstTimeUserActivity : AppCompatActivity() {
    //TODO:put in @string
    private val sharedPreferenceName = "pref"
    private val sharedPreferenceKeyFirstTimeUser = "isFirstTimeUser"

    private val sharedPreferencesKey = "com.example.myapp.MODE"
    private val sharedPreferencesKeyClassicSong = "com.example.myapp.CLASSICSONG"
    private val sharedPreferencesKeyCurrentSong = "com.example.myapp.CURRENTSONG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_user)

        Log.d("TAG", "It's a first time user")
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

    private fun savePrefRandSong(song: String, mode: String) {
        val sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()

        if (mode == "classic") {
            editor.putString(sharedPreferencesKeyClassicSong, song)
        } else {
            editor.putString(sharedPreferencesKeyCurrentSong, song)
        }

        editor.commit()
    }

    fun chooseClassic(view: View) {
        val  randSong = selectRandSong("classic")

        //save preferences
        savePrefMode("classic")
        savePrefRandSong(randSong, "classic")

        Log.d("TAG", "START with mode classic and song $randSong")

        // update first time user
        getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE).edit()
            .putBoolean(sharedPreferenceKeyFirstTimeUser, false).apply()

        val intent = Intent(applicationContext, MapsActivity::class.java)
        startActivity(intent)
    }

    fun chooseCurrent(view: View) {
        val randSong = selectRandSong("current")

        savePrefMode("current")
        savePrefRandSong(randSong, "current")

        Log.d("TAG", "START with mode current and song $randSong")

        // update first time user
        getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE).edit()
            .putBoolean(sharedPreferenceKeyFirstTimeUser, false).apply()

        val intent = Intent(applicationContext, MapsActivity::class.java)
        startActivity(intent)
    }
}
