package com.example.cs306_coursework

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import com.google.android.material.bottomappbar.BottomAppBar
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.Menu
import android.view.MenuItem
import android.view.View


class MainActivity : AppCompatActivity() {
    private val sharedPreference = "pref"
    private val sharedPreferenceKey = "isFirstTimeUser"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkFirstTimeUser()

        // handle menu options for bottom app bar
        val bar = findViewById<BottomAppBar>(R.id.bar)
        bar.setNavigationOnClickListener {
            Toast.makeText(this@MainActivity, "Navig menu btn click", Toast.LENGTH_LONG)
                .show()
            // Handle the navigation click by showing a BottomDrawer etc.
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
        }

        // handle fab options for bottom app bar
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Toast.makeText(this@MainActivity, "FAB  btn click", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun checkFirstTimeUser() {

        val isFirstRun = getSharedPreferences(sharedPreference, Context.MODE_PRIVATE)
            .getBoolean(sharedPreferenceKey, true)

        if (isFirstRun) {
            startActivity(Intent(this@MainActivity, FirstTimeUserActivity::class.java))
            Toast.makeText(this@MainActivity, "Run only once", Toast.LENGTH_LONG)
                .show()
        }

        getSharedPreferences(sharedPreference, Context.MODE_PRIVATE).edit()
            .putBoolean(sharedPreferenceKey, false).apply()
    }

    fun showFavSongs(item: MenuItem) {
        Toast.makeText(this@MainActivity, "Fav btn click", Toast.LENGTH_LONG)
            .show()
    }
}
