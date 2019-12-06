package com.example.cs306_coursework

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*

class FavouriteSongsListAdapter(private val foundSongsModelArrayList: MutableList<FoundSongsListModel>, private val appData: AppData ) :
    RecyclerView.Adapter<FavouriteSongsListAdapter.ViewHolder>() {

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
        val songModel = foundSongsModelArrayList[position]
        //show text and liked button
        holder.txtMsg.text = songModel.title + "\n" + songModel.artist
        holder.imgView.setImageResource(songModel.image_status_drawable)

    }
    override fun getItemCount(): Int {
        return foundSongsModelArrayList.size
    }

}