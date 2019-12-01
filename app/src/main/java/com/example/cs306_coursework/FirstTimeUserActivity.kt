package com.example.cs306_coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class FirstTimeUserActivity : AppCompatActivity() {
    var ad:AppData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_user)

        ad = AppData.getInstance(this)

        Log.d("TAG", "It's a first time user")
    }

    private fun selectRandSong(mode: String): String {
        return application.assets.list(mode).random()
    }

    private fun changeActivity() {
        val intent = Intent(applicationContext, MapsActivity::class.java)
        startActivity(intent)
    }

    fun chooseClassic(view: View) {
        val  randSong = selectRandSong("classic")

        // save preferences
        ad?.savePrefModeAndSong("classic", randSong)

        Log.d("TAG", "START with mode classic and song $randSong")

        // update first time user
        ad?.savePrefFirstTime()

        changeActivity()
    }

    fun chooseCurrent(view: View) {
        val randSong = selectRandSong("current")

        // save preferences
        ad?.savePrefModeAndSong("current", randSong)

        Log.d("TAG", "START with mode current and song $randSong")

        // update first time user
        ad?.savePrefFirstTime()

        changeActivity()
    }
}
