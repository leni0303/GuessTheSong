package com.example.cs306_coursework

import android.content.Context
import android.content.Intent
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

    // current mode and song
    var song: String? = null
    var mode: String? = null

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

        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                createMarkerList(currentLatLng, 500)
                addMarkerListToMap()

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            } else {
                //TODO: show message that location is not available
                Log.d("TAG", "Location is not enabled")
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
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
        var previousLyricsList: MutableList<String> = emptyList<String>().toMutableList()

        for(x in 0..10) {

            // get a random lyrics
            randomLyric = listOfLyrics.random()
            // what do we do with lyrics that repeat each other
            // we get non-repeating lyrics and show all of the repeating lyrics inside text once unlocked
            //TODO: check of lyric is not equal to guessed lyric
            while (previousLyricsList.contains(randomLyric)) {
                Log.d("TAG", "both lyrics are equal")
                randomLyric = listOfLyrics.random()
            }

            val marker = MarkerData(createRandomMarkerLatLng(location,radius), randomLyric)
            Log.d("TAG", "marker object ${marker.title}")
            markerList.add(marker)

            // collect already shown lyrics
            previousLyricsList.add(randomLyric)
        }

        Log.d("TAG", "marker list size ${markerList.size}")
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

        if (isFirstRun) {
            startActivity(Intent(this, FirstTimeUserActivity::class.java))
            Toast.makeText(this, "Run only once", Toast.LENGTH_LONG)
                .show()
        }

        getSharedPreferences(sharedPreference, Context.MODE_PRIVATE).edit()
            .putBoolean(sharedPreferenceKey, false).apply()

        //saveSongInList()
        updateModeAndSong()
    }

    fun updateModeAndSong() {
        val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val currentMode = sharedpreferences.getString("com.example.myapp.MODE", null)
        val currentSong = sharedpreferences.getString("com.example.myapp.SONG", null)
        song = currentSong
        mode = currentMode
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

        //TODO: Update local mode
    }

    fun clickChangeSong(item: MenuItem) {
        //TODO: change with local varianles
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

        //saveSongInList()
        //TODO: update only song
        updateModeAndSong()
    }
    fun clickSongList(item: MenuItem) {
        Toast.makeText(this, "Song List", Toast.LENGTH_LONG)
            .show()
    }
    fun clickQuitApp(item: MenuItem) {
        finishAndRemoveTask()
    }
}
