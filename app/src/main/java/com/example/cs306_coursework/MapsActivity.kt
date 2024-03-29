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

import com.google.android.gms.maps.model.Marker
import android.provider.Settings
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.database.*

import kotlin.collections.HashSet

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    // access to system location services
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var database: DatabaseReference

    // a set of markers on the map
    private val mapMarkerList =  emptyList<Marker>().toMutableList()

    // in meters
    private var radius = 2000
    // cap on markers number to be spawned on map
    private var numMarkers = 10
    // zoom for camera
    private var zoom = 14f
    // in km
    private var minDistance = 0.60

    private var ad:AppData? = null

    var markerStatText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // initialise AppData
        ad = AppData.getInstance(this)
        database = FirebaseDatabase.getInstance().reference
        ad!!.debug()

        // check if it's a first time user
        ad!!.checkFirstTimeUser(Intent(this, FirstTimeUserActivity::class.java))

        markerStatText = findViewById(R.id.marker_num_text)

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

        // reset song status
        if (intent.extras != null) {
            if (intent.extras.getString("songStatus") == "guessed") {
                Log.d("TAG","spawn new song!!")
                changeSong()

                intent.extras.putString("songStatus", "not")
            }
        }

    }

    /*------------------------------------MAP Activities ------------------------------------*/
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

        // see if app has permission
        askLocationPermission()

        if (!ad!!.firstTimeUser) {
            // get current song and mode as a local variable
            mMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // spawn markers on map
                    setUpMap(location)
                    // get a number of found lyrics and total lyrics shown on map
                    showMarkerStats()
                }
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        // get current location
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                val distancePlayerMarker = AppCalculations.findDistance(LatLng(location.latitude, location.longitude), p0!!.position)

                // hide title
                p0.hideInfoWindow()

                // check if player is close enough to marker
                if (distancePlayerMarker < minDistance) {
                    Toast.makeText(this, p0.title, Toast.LENGTH_LONG)
                        .show()

                    // save marker to preferences
                    ad!!.saveLyric(p0!!.title)
                    ad!!.debug()
                    // remove marker from the map and list
                    for (x in mapMarkerList) {
                        if (x.id == p0.id){
                            x.remove()
                            mapMarkerList.remove(x)
                            break
                        }
                    }
                    showMarkerStats()
                } else {
                    Toast.makeText(this, R.string.get_closer, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        return false
    }

    /**
     * add markers to map relative to current location
     */
    private fun setUpMap(location: Location) {
        // current location
        val currentLatLng = LatLng(location.latitude, location.longitude)
        // create a marker list and add individual markers to map
        addMarkerListToMap(createMarkerList(currentLatLng, radius))

        if(mMap.cameraPosition.zoom < zoom) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom))
        }
    }

    /**
     * create a marker with random lyrics and random location
     */
    private fun createMarkerList(location: LatLng, radius: Int): MutableList<MarkerData> {

        // create a set of unique lyrics
        val lyricSet = HashSet<String>(SongDatabase.readSongLyricsAsList(this, ad!!.mode, ad!!.song))
        // remove found lyrics
        ad!!.removeLyricFromSet(lyricSet)

        //var i = 0
        val markerList =  emptyList<MarkerData>().toMutableList()

        for(randLyric in lyricSet.shuffled()) {

            // create only n markers
            //if (i > numMarkers)
            //    break

            // create a marker with a random location
            val marker = MarkerData(AppCalculations.createRandomMarkerLocation(location,radius), randLyric)
            markerList.add(marker)

            //i++
        }

        return markerList
    }


    /**
     * spawn marker on map
     */
    private fun addMarkerListToMap(markerList: MutableList<MarkerData>) {
        for (x in 0 until markerList.size) {
            mapMarkerList.add(addMarkerToMap(markerList[x].latLng, markerList[x].title))
        }
    }

    /**
     * create a marker object
     */
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

    /**
     * remove marker
     */
    private fun clearMarkers() {
        for (x in mapMarkerList) {
            x.remove()
        }
    }
    /*------------------------------------MAPS Activities------------------------------------*/

    /**
     * Take user to setting to enable location permission
     */
    private fun askLocationPermission() {
        // Permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // send user to settings permission
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    /**
     * show found markers and number of markers shown on the map
     */
    private fun showMarkerStats() {
        val currentProgress:Int = if(ad!!.mode == "classic") {
            ad!!.foundLyricsClassic.size
        } else {
            ad!!.foundLyricsCurrent.size
        }
        val total = mapMarkerList.size

        markerStatText!!.text = "$currentProgress / $total"
    }

    /*------------------------------------Handle Btn Clicks------------------------------------*/

    // button action for bottom app items
    fun fabBtnClick(view: View) {
        this.startActivity(Intent(this, GuessSongActivity::class.java))
    }

    fun showMostPopularSongs(item: MenuItem) {
        this.startActivity(Intent(this, MostPopularSongsActivity::class.java))
    }

    // button action for menu items
    fun clickFavouriteSongs(item: MenuItem) {
        this.startActivity(Intent(this, FavouriteSongListActivity::class.java))
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
                    // spawn new markers
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
                    // spawn new markers
                    setUpMap(location)
                }
            }
        }
        showMarkerStats()
    }

    fun clickChangeSong(item: MenuItem) {
        // select a random song
        changeSong()
    }

    private fun changeSong() {
        // select a random song
        var randomSong = SongDatabase.selectRandSong(this, ad!!.mode)

        // keep picking a random song if equal to previous one or to found songs
        while(randomSong == ad!!.song && (!ad!!.foundSongs.contains(randomSong))) {
            randomSong = SongDatabase.selectRandSong(this, ad!!.mode)
        }

        // update song
        ad!!.savePrefSong(randomSong)
        // clear list of found lyrics
        ad!!.clearLyrics()

        showMarkerStats()
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

    fun clickSongList(item: MenuItem) {
        this.startActivity(Intent(this, FoundSongsListActivity::class.java))
    }
    fun clickQuitApp(item: MenuItem) {
        finishAndRemoveTask()
    }
}
