package com.example.cs306_coursework

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView

class MostPopularSongAdapter(private val context: Context,
                             private val dataSource: ArrayList<FirebaseDatabaseManager.Song>): BaseAdapter() {

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val inflater = LayoutInflater.from(parent.context)
        val rowView = inflater.inflate(R.layout.most_popular_song_row_layout, parent, false)

        val songName = rowView.findViewById(R.id.popularSongName) as AppCompatTextView

        val song = getItem(position) as FirebaseDatabaseManager.Song
        songName.text = song.name + "\n" + "Likes: " + song.likes

        return rowView
    }


}