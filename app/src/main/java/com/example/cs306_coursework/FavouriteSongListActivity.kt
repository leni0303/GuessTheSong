package com.example.cs306_coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavouriteSongListActivity : AppCompatActivity() {

    private var ad:AppData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_song_list)

        ad = AppData.getInstance(this)

        val favouriteSongsArrayList: ArrayList<FoundSongsListModel> = populateList()

        val recyclerView = findViewById<View>(R.id.favourite_songs_recycler_view) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val mAdapter = FavouriteSongsListAdapter(favouriteSongsArrayList, ad!!)
        recyclerView.adapter = mAdapter
    }

    /**
     * populate array with favourite songs
     */
    private fun populateList(): ArrayList<FoundSongsListModel> {

        var song: Pair<CharSequence, CharSequence>
        val list = ArrayList<FoundSongsListModel>()

        for (x in ad!!.favouriteSongs) {
            song = SongDatabase.readArtistAndSong(x)

            val songListModel = FoundSongsListModel()
            songListModel.artist = song.first.toString()
            songListModel.title = song.second.toString()
            songListModel.id = x

            songListModel.image_status_drawable = R.drawable.heart_black

            list.add(songListModel)
        }
        return list
    }
}
