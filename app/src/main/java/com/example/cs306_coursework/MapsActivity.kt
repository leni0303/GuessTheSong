package com.example.cs306_coursework

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomappbar.BottomAppBar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val sharedPreference = "pref"
    private val sharedPreferenceKey = "isFirstTimeUser"
    var songLyricsList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    // read a specific song from specific mode from assets
    private fun readSongLyrics(mode: String, fileName: String): String {
        return application.assets.open("$mode/$fileName").bufferedReader().use { it.readText()}
    }

    private fun readSongLyricsAsList(mode: String, fileName: String): List<String> {
        return application.assets.open("$mode/$fileName").bufferedReader().readLines()
    }

    private fun saveSongInList() {
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
            startActivity(Intent(this, FirstTimeUserActivity::class.java))
            Toast.makeText(this, "Run only once", Toast.LENGTH_LONG)
                .show()
        }

        getSharedPreferences(sharedPreference, Context.MODE_PRIVATE).edit()
            .putBoolean(sharedPreferenceKey, false).apply()
    }

    /*--------------------Handle Btn Clicks--------------------*/
    // button action for bottom app items
    fun fabBtnClick(view: View) {
        Toast.makeText(this, "FAB  btn click", Toast.LENGTH_LONG)
            .show()
    }

    fun showFavSongs(item: MenuItem) {
        Toast.makeText(this, "Fav btn click", Toast.LENGTH_LONG)
            .show()
    }

    // button action for menu items
    fun clickTopSongs(item: MenuItem) {
        Toast.makeText(this, "Top Songs", Toast.LENGTH_LONG)
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

        saveSongInList()
    }
    fun clickSongList(item: MenuItem) {
        Toast.makeText(this, "Song List", Toast.LENGTH_LONG)
            .show()
    }
    fun clickQuitApp(item: MenuItem) {
        finishAndRemoveTask()
    }
}
