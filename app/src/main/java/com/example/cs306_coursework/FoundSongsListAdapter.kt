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

class FoundSongsListAdapter(private val foundSongsModelArrayList: MutableList<FoundSongsListModel>, private val appData: AppData ) :
    RecyclerView.Adapter<FoundSongsListAdapter.ViewHolder>() {

    private lateinit var database: DatabaseReference

    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var txtMsg: TextView
        var imgView: ImageView

        init {
            txtMsg = layout.findViewById<View>(R.id.songName) as TextView
            imgView = layout.findViewById<View>(R.id.fav_songs) as ImageView

            database = FirebaseDatabase.getInstance().reference
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.song_row_layout, parent, false)
        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val songModel = foundSongsModelArrayList[position]

        holder.txtMsg.text = songModel.title + "\n" + songModel.artist
        holder.imgView.setImageResource(songModel.image_status_drawable)
        holder.txtMsg.setOnClickListener {v ->

            if (appData.favouriteSongs.contains(songModel.id)) {
                // remove song from favourites
                appData.removeFavouriteSong(songModel.id)
                //update firebase db
                FirebaseDatabaseManager.removeLike(database, songModel)
                // update image
                holder.imgView.setImageResource(R.drawable.outline_heart)
                Snackbar.make(v, "Song ${songModel.title} has been removed from favs", Snackbar.LENGTH_LONG).show()
            } else {
                // add song to favourites
                appData.saveFavouriteSong(songModel.id)
                // update image
                holder.imgView.setImageResource(R.drawable.heart_black)
                // update firebase song
                FirebaseDatabaseManager.addLike(database, songModel)
                Snackbar.make(v, "Song ${songModel.title} has been added to favs", Snackbar.LENGTH_LONG).show()
            }
        }
    }
    override fun getItemCount(): Int {
        return foundSongsModelArrayList.size
    }

}