package com.example.cs306_coursework

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private val sharedPreference = getString(R.string.sharedPreference)
    private val sharedPreferenceKey = getString(R.string.sharedPreferenceKey)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkFirstTimeUser()
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
}
