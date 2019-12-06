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
    }

    /**
     * change to the main activity
     */
    private fun changeActivity() {
        val intent = Intent(applicationContext, MapsActivity::class.java)
        startActivity(intent)
    }

    fun chooseClassic(view: View) {
        val  randSong = SongDatabase.selectRandSong(this,"classic")

        // save preferences
        ad?.savePrefModeAndSong("classic", randSong)

        // update first time user
        ad?.savePrefFirstTime()

        changeActivity()
    }

    fun chooseCurrent(view: View) {
        val randSong = SongDatabase.selectRandSong(this, "current")

        // save preferences
        ad?.savePrefModeAndSong("current", randSong)

        // update first time user
        ad?.savePrefFirstTime()

        changeActivity()
    }
}
