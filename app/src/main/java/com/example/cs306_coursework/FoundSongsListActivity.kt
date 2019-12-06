package com.example.cs306_coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FoundSongsListActivity : AppCompatActivity() {

    private var ad:AppData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        ad = AppData.getInstance(this)

        val foundSongsArrayList: ArrayList<FoundSongsListModel> = populateList()

        val recyclerView = findViewById<View>(R.id.my_recycler_view) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val mAdapter = FoundSongsListAdapter(foundSongsArrayList, ad!!)
        recyclerView.adapter = mAdapter
    }

    /**
     * create a list of found songs
     */
    private fun populateList(): ArrayList<FoundSongsListModel> {

        var song: Pair<CharSequence, CharSequence>
        val list = ArrayList<FoundSongsListModel>()

        for (x in ad!!.foundSongs) {
            song = SongDatabase.readArtistAndSong(x)

            // create a model of a song
            val songListModel = FoundSongsListModel()
            songListModel.artist = song.first.toString()
            songListModel.title = song.second.toString()
            songListModel.id = x

            // song is in favourite list
            if(ad!!.favouriteSongs.contains(x)) {
                songListModel.image_status_drawable = R.drawable.heart_black
            } else {
                //song is not in favourite list
                songListModel.image_status_drawable = R.drawable.outline_heart
            }

            list.add(songListModel)
        }
        return list
    }
}
