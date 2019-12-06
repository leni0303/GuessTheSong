package com.example.cs306_coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.ListView

class MostPopularSongsActivity : AppCompatActivity() {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    var listItems = ArrayList<FirebaseDatabaseManager.Song>()

    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_most_popular_songs)

        database = FirebaseDatabase.getInstance().reference
        listView = findViewById<ListView>(R.id.list)

        val adapter = MostPopularSongAdapter(this, listItems)
        listView.adapter = adapter

        FirebaseDatabaseManager.getMostPopular(database) {
                psongs -> for (x in psongs.reversed()){
            listItems.add(x)
            adapter.notifyDataSetChanged()

        }
        }
    }
}
