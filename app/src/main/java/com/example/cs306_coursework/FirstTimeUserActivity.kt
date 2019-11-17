package com.example.cs306_coursework

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class FirstTimeUserActivity : AppCompatActivity() {
    private val sharedPreferenceName = getString(R.string.sharedPreference)
    private val sharedPreferencesKey = "mode"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_user)

    }

    fun chooseClassic(view: View) {
        val sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()

        editor.putString(sharedPreferencesKey, "classic")
        editor.commit()

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    fun chooseCurrent(view: View) {
        val sharedpreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()

        editor.putString(sharedPreferencesKey, "current")
        editor.commit()

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}
