package com.example.cs306_coursework

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

import kotlin.collections.HashSet

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    // access to system location services
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // a set of markers on the map
    private val mapMarkerList =  emptyList<Marker>().toMutableList()

    // in meters
    private var radius = 2000
    private var numMarkers = 10
    private var zoom = 12f
    // in km
    private var minDistance = 0.60

    private var ad:AppData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // initialise AppData
        ad = AppData.getInstance(this)
        ad!!.debug()

        // check if it's a first time user
        ad!!.checkFirstTimeUser(Intent(this, FirstTimeUserActivity::class.java))

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

        if (intent.extras != null) {
            if (intent.extras.getString("songStatus") == "guessed") {
                Log.d("TAG","spawn new song!!")
                changeSong()
            }
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

        if (!ad!!.firstTimeUser) {
            // get current song and mode as a local variable
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    setUpMap(location)
                }
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        //TODO: make marker unclickable if it's too far away
        // make a better method for current location

        // get current location
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                val distancePlayerMarker = AppCalculations.findDistance(LatLng(location.latitude, location.longitude), p0!!.position)
                Log.d("TAG", "distance between $distancePlayerMarker")

                if (distancePlayerMarker < minDistance) {
                    Toast.makeText(this, p0.title, Toast.LENGTH_LONG)
                        .show()

                    ad!!.saveLyric(p0!!.title)
                    ad!!.debug()
                    for (x in mapMarkerList) {
                        if (x.id == p0.id){
                            x.remove()
                            mapMarkerList.remove(x)
                            break
                        }
                    }

                } else {
                    Toast.makeText(this, "Get closer to marker", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        return false
    }

    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // TODO: show message that location is not available
            Log.d("TAG", "Location is not enabled")
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun setUpMap(location: Location) {

        val currentLatLng = LatLng(location.latitude, location.longitude)

        addMarkerListToMap(createMarkerList(currentLatLng, radius))

        if(mMap.cameraPosition.zoom < zoom) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom))
        }
    }

    private fun createMarkerList(location: LatLng, radius: Int): MutableList<MarkerData> {

        // create a set of unique lyrics
        val lyricSet = HashSet<String>(SongDatabase.readSongLyricsAsList(this, ad!!.mode, ad!!.song))
        // remove found lyrics
        //lyricSet.removeAll(ad!!.foundLyrics)
        ad!!.removeLyricFromSet(lyricSet)

        ad!!.debug()
        Log.d("TAG", "set on map $lyricSet")

        var i = 0
        val markerList =  emptyList<MarkerData>().toMutableList()

        for(randLyric in lyricSet.shuffled()) {

            // create only n markers
            //if (i > numMarkers)
            //    break

            val marker = MarkerData(AppCalculations.createRandomMarkerLocation(location,radius), randLyric)
            markerList.add(marker)

            i++
        }

        return markerList
    }


    private fun addMarkerListToMap(markerList: MutableList<MarkerData>) {
        for (x in 0 until markerList.size) {
            mapMarkerList.add(addMarkerToMap(markerList[x].latLng, markerList[x].title))
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

    private fun clearMarkers() {
        for (x in mapMarkerList) {
            x.remove()
        }
    }
    /*------------------------------------MAPS END------------------------------------*/

    /*--------------------Handle Btn Clicks--------------------*/
    // button action for bottom app items
    fun fabBtnClick(view: View) {
        this.startActivity(Intent(this, GuessSongActivity::class.java))
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

        if (ad!!.mode == "classic") {
            // switch to current
            // update mode
            ad!!.savePrefMode("current")

            // get song for this mode
            ad!!.syncSong()

            ad!!.debug()

            // clear previous markers and update new ones
            clearMarkers()
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    setUpMap(location)
                }
            }
        } else {
            // switch to classic
            // update mode
            ad!!.savePrefMode("classic")

            // get song for this mode
            ad!!.syncSong()

            ad!!.debug()

            // clear previous markers and update new ones
            clearMarkers()
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    setUpMap(location)
                }
            }
        }
    }

    fun clickChangeSong(item: MenuItem) {
        // select a random song
        changeSong()
    }

    private fun changeSong() {
        var randomSong = SongDatabase.selectRandSong(this, ad!!.mode)

        Log.d("TAG", "random song $randomSong")

        // keep picking a random song if equal to previous one
        while(randomSong == ad!!.song && (!ad!!.foundSongs.contains(randomSong))) {
            randomSong = SongDatabase.selectRandSong(this, ad!!.mode)
        }

        // update song
        ad!!.savePrefSong(randomSong)

        ad!!.debug()
        ad!!.clearLyrics()

        // clear previous markers and update new ones
        clearMarkers()
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                setUpMap(location)
            }
        }
    }

    fun clickSongList(item: MenuItem) {
        Toast.makeText(this, "Song List", Toast.LENGTH_LONG)
            .show()
    }
    fun clickQuitApp(item: MenuItem) {
        finishAndRemoveTask()
    }
}
