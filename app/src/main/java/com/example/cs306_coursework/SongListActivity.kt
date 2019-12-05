package com.example.cs306_coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongListActivity : AppCompatActivity() {

    private var ad:AppData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        ad = AppData.getInstance(this)

        var songArrayList: ArrayList<SongListModel> = populateList()

        val recyclerView = findViewById<View>(R.id.my_recycler_view) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val mAdapter = SongAdapter(songArrayList, ad!!)
        recyclerView.adapter = mAdapter
    }

    private fun populateList(): ArrayList<SongListModel> {

        var song: Pair<CharSequence, CharSequence>
        val list = ArrayList<SongListModel>()

        for (x in ad!!.foundSongs) {
            song = SongDatabase.readArtistAndSong(x)

            val songListModel = SongListModel()
            songListModel.setSongArtist(song.first.toString())
            songListModel.setSongTitle(song.second.toString())
            songListModel.setSongid(x)

            if(ad!!.favouriteSongs.contains(x)) {
                songListModel.setImage_drawables(R.drawable.heart_black)
            } else {
                songListModel.setImage_drawables(R.drawable.outline_heart)
            }

            list.add(songListModel)
        }
        return list
    }
}
