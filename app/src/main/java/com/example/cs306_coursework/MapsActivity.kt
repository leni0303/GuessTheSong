package com.example.cs306_coursework

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.*

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.gms.maps.model.Marker


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // create a list of markers
    val markerList =  emptyList<MarkerData>().toMutableList()

    private val sharedPreference = "pref"
    private val sharedPreferenceKey = "isFirstTimeUser"
    //var songLyricsList: List<String>? = null

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
        //Get last location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                var loc = location
                Log.d("TAG","location" + loc.toString())
            }

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

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(51.619543, -3.878634)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Computational Foundry"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        createMarkerList()
        addMarkerListToMap()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        Log.d("TAG", p0?.title)
        return false
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getUpdatedLocation() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 2000
        mLocationRequest.fastestInterval = 1000
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            var lat = mLastLocation.latitude
            var long = mLastLocation.longitude
            val lastLoc = LatLng(lat, long)
        }
    }

    fun createMarkerList() {
        //list if lat and long
        val latitude: DoubleArray = doubleArrayOf(51.619543, 51.618810, 51.617316)
        val longitude: DoubleArray = doubleArrayOf(-3.878634, -3.878517,  -3.878383)

        // create a list of lyrics
        val listOfLyrics = readSongLyricsAsList(mode, song)

        // create a random lyric and previous lyric
        var randomLyric: String
        var previousLyric: String? = null

        for(x in 0..2) {

            // get a random lyrics
            randomLyric = listOfLyrics.random()
            // what do we do with lyrics that repeat each other
            // we get non-repeating lyrics and show all of the repeating lyrics inside text once unlocked

            while (randomLyric == previousLyric) {
                Log.d("TAG", "both lyrics are equal")
                randomLyric = listOfLyrics.random()
            }

            val marker = MarkerData(latitude[x],longitude[x], randomLyric)
            Log.d("TAG", "marker object ${marker.title}")
            markerList.add(marker)

            previousLyric = randomLyric
        }

        Log.d("TAG", "marker list size ${markerList.size}")
    }

    protected fun addMarkerToMap(
        latitude: Double,
        longitude: Double,
        title: String
    ): Marker {
        return mMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .title(title)
        )
    }

    fun addMarkerListToMap() {
        for (x in 0 until markerList.size) {
            addMarkerToMap(markerList[x].latitutde, markerList[x].longitude, markerList[x].title)
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

        //saveSongInList()
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
