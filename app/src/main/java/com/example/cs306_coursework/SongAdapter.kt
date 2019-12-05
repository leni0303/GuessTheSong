package com.example.cs306_coursework

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class SongAdapter(private val songModelArrayList: MutableList<SongListModel>, private val appData: AppData ) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var txtMsg: TextView
        var imgView: ImageView

        init {
            txtMsg = layout.findViewById<View>(R.id.songName) as TextView
            imgView = layout.findViewById<View>(R.id.fav_songs) as ImageView
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.song_row_layout, parent, false)
        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = songModelArrayList[position]

        val songInfo = info.getSongTitle() + "\n" + info.getSongArtist()

        holder.txtMsg.text = songInfo
        holder.imgView.setImageResource(info.getImage_drawables())
        holder.txtMsg.setOnClickListener {v ->

            if (appData.favouriteSongs.contains(info.getSongId())) {
                appData.removeFavouriteSong(info.getSongId())

                holder.imgView.setImageResource(R.drawable.outline_heart)
                val snackbar = Snackbar.make(v, "Song ${info.getSongTitle()} has been removed from favs", Snackbar.LENGTH_LONG)
                snackbar.show()

            } else {

                appData.saveFavouriteSong(info.getSongId())
                holder.imgView.setImageResource(R.drawable.heart_black)

                val snackbar = Snackbar.make(
                    v,
                    "Song ${info.getSongTitle()} has been added to favs",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }
        }
    }
    override fun getItemCount(): Int {
        return songModelArrayList.size
    }

}