package com.example.cs306_coursework

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.gms.maps.model.Marker
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    // access to system location services
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    // create a list of markers
    val markerList =  emptyList<MarkerData>().toMutableList()

    //TODO: change with @string text
    private val sharedPreference = "pref"
    private val sharedPreferenceKey = "isFirstTimeUser"
    private val sharedPreferencesKeyClassicSong = "com.example.myapp.CLASSICSONG"
    private val sharedPreferencesKeyCurrentSong = "com.example.myapp.CURRENTSONG"

    // current mode and song
    var song: String? = null
    var mode: String? = null

    //first time user value
    var isFirstTimeUser: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // check if it's a first time user
        checkFirstTimeUser()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Create location services Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // handle menu options for bottom app bar
        val bar = findViewById<BottomAppBar>(R.id.bar)
        bar.setNavigationOnClickListener {
            // show menu items
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
        }
    }

    /*------------------------------------MAPS Activities START------------------------------------*/
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
        mMap.setOnMarkerClickListener(this)

        askLocationPermission()
        if (!isFirstTimeUser) {
            // get current song and mode as a local variable
            syncModeAndSong()
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                Log.d("TAG", "Location mode $mode, song $song")
                createMarkerList(currentLatLng, 500)
                addMarkerListToMap()

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // TODO: show message that location is not available
            Log.d("TAG", "Location is not enabled")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
       // Log.d("TAG", p0?.title)
        return false
    }

    private fun createRandomMarkerLatLng(location: LatLng, radius: Int): LatLng {
        val random = Random()

        // Convert radius from meters to degrees
        val radiusInDegrees = (radius / 111000f).toDouble()

        val u = random.nextDouble()
        val v = random.nextDouble()
        val w = radiusInDegrees * sqrt(u)
        val t = 2.0 * Math.PI * v
        val x = w * cos(t)
        val y = w * sin(t)

        // Adjust the x-coordinate for the shrinking of the east-west distances
        val newX = x / cos(Math.toRadians(location.latitude))

        val foundLongitude = newX + location.longitude
        val foundLatitude = y + location.latitude

        return  LatLng(foundLatitude, foundLongitude)
    }

    private fun createMarkerList(location: LatLng, radius: Int) {

        // create a list of lyrics
        val listOfLyrics = readSongLyricsAsList(mode, song)

        // create a random lyric and previous lyric
        var randomLyric: String
        val previousLyricsList: MutableList<String> = emptyList<String>().toMutableList()

        for(x in 0..9) {

            // get a random lyrics
            randomLyric = listOfLyrics.random()
            // what do we do with lyrics that repeat each other
            // we get non-repeating lyrics and show all of the repeating lyrics inside text once unlocked
            //TODO: check of lyric is not equal to guessed lyric
            while (previousLyricsList.contains(randomLyric)) {
                randomLyric = listOfLyrics.random()
            }

            val marker = MarkerData(createRandomMarkerLatLng(location,radius), randomLyric)
            markerList.add(marker)

            // collect already shown lyrics
            previousLyricsList.add(randomLyric)
        }
    }

    private fun addMarkerToMap(
        latLng: LatLng,
        title: String
    ): Marker {
        return mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
        )
    }

    private fun addMarkerListToMap() {
        for (x in 0 until markerList.size) {
            addMarkerToMap(markerList[x].latLng, markerList[x].title)
        }
    }
    /*------------------------------------MAPS END------------------------------------*/


    // read a specific song from specific mode from assets
    private fun readSongLyrics(mode: String, fileName: String): String {
        return application.assets.open("$mode/$fileName").bufferedReader().use { it.readText()}
    }

    private fun readSongLyricsAsList(mode: String?, fileName: String?): List<String> {
        return application.assets.open("$mode/$fileName").bufferedReader().readLines()
    }

   /* private fun saveSongInList() {
        val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val currentMode = sharedpreferences.getString("com.example.myapp.MODE", null)
        val currentSong = sharedpreferences.getString("com.example.myapp.SONG", null)

        Log.d("TAG", "mode $currentMode, song $currentSong")
        songLyricsList = readSongLyricsAsList(currentMode, currentSong)
    }*/

    private fun selectRandSong(mode: String): String {
        return application.assets.list(mode).random()
    }

    // check if user opens the app for the first time
    private fun checkFirstTimeUser() {
        val isFirstRun = getSharedPreferences(sharedPreference, Context.MODE_PRIVATE)
            .getBoolean(sharedPreferenceKey, true)
        Log.d("TAG", "isFirstRun $isFirstRun")
        isFirstTimeUser = isFirstRun

        if (isFirstRun) {
            startActivity(Intent(this, FirstTimeUserActivity::class.java))
        }
    }

    fun syncModeAndSong() {
        val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val currentMode = sharedpreferences.getString("com.example.myapp.MODE", null)
        mode = currentMode

        if (mode == "classic") {
            val currentSong = sharedpreferences.getString("com.example.myapp.CLASSICSONG", null)
            song = currentSong
            Log.d("TAG", "Update song to $song")
        } else {
            val currentSong = sharedpreferences.getString("com.example.myapp.CURRENTSONG", null)
            song = currentSong
            Log.d("TAG", "Update song to $song")
        }
    }

    private fun saveNewSong(song: String, mode: String) {
        val sharedpreferences = getSharedPreferences(sharedPreference, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()

        if (mode == "classic") {
            Log.d("TAG", "Save new song for classic $song")
            editor.putString(sharedPreferencesKeyClassicSong, song)
        } else {
            Log.d("TAG", "Save new song for current $song")
            editor.putString(sharedPreferencesKeyCurrentSong, song)
        }

        editor.commit()
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
        val editor = sharedpreferences.edit()

        // switch to current
        if (mode == "classic") {
            // update mode
            editor.putString("com.example.myapp.MODE", "current")
            editor.commit()

            mode = "current"

            //TODO: Choose new song or resume with previous for current
            // get a saved song for this mode
            val modeSong =  sharedpreferences.getString("com.example.myapp.CURRENTSONG", null)

            if (modeSong == null) {
                // generate a new song for this mode
                song = selectRandSong(mode!!)
                // update new song
                saveNewSong(song!!, mode!!)
                // update local variable
                syncModeAndSong()
            }else {
                // update local variable
                song = modeSong

                Log.d("TAG", "previous song from mode $song")

            }

        } else {
            // switch to classic
            editor.putString("com.example.myapp.MODE", "classic")
            editor.commit()

            mode = "classic"

            //TODO: Choose new song or resume with previous
            // get a saved song for this mode
            val modeSong =  sharedpreferences.getString("com.example.myapp.CLASSICSONG", null)

            if (modeSong == null) {
                song = selectRandSong(mode!!)
                // update new song
                saveNewSong(song!!, mode!!)
                // update local variable
                syncModeAndSong()

            }else {
                // update local variable
                song = modeSong

            }
        }
    }

    fun clickChangeSong(item: MenuItem) {
        val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()

        var randomSong = mode?.let { selectRandSong(it) }
        while(randomSong == song) {
            randomSong = mode?.let { selectRandSong(it) }
        }

        if (mode == "classic") {
            editor.putString("com.example.myapp.CLASSICSONG", randomSong)
        }else {
            editor.putString("com.example.myapp.CURRENTSONG", randomSong)
        }

        editor.commit()
        song = randomSong
    }

    fun clickSongList(item: MenuItem) {
        Toast.makeText(this, "Song List", Toast.LENGTH_LONG)
            .show()
    }
    fun clickQuitApp(item: MenuItem) {
        finishAndRemoveTask()
    }
}
