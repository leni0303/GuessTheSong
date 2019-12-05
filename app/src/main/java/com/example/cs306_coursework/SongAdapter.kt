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

class SongAdapter(private val songModelArrayList: MutableList<SongListModel>, private val appData: AppData ) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

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
        val info = songModelArrayList[position]

        val songInfo = info.getSongTitle() + "\n" + info.getSongArtist()
        val firebaseSongId = info.getSongId().removeSuffix(".txt")

        holder.txtMsg.text = songInfo
        holder.imgView.setImageResource(info.getImage_drawables())
        holder.txtMsg.setOnClickListener {v ->

            if (appData.favouriteSongs.contains(info.getSongId())) {
                // remove song from favourites
                appData.removeFavouriteSong(info.getSongId())

                //update firebase db
                FirebaseDatabaseManager.readLikes(database, firebaseSongId, info.getSongTitle()) {
                        value -> FirebaseDatabaseManager.updateLikes(database, firebaseSongId, info.getSongTitle(), value - 1 )
                }

                holder.imgView.setImageResource(R.drawable.outline_heart)
                Snackbar.make(v, "Song ${info.getSongTitle()} has been removed from favs", Snackbar.LENGTH_LONG).show()
            } else {
                // add song to favourites
                appData.saveFavouriteSong(info.getSongId())
                holder.imgView.setImageResource(R.drawable.heart_black)

                Log.d("db", "click")
                // update firebase song
                FirebaseDatabaseManager.readLikes(database, firebaseSongId, info.getSongTitle()) {
                        value -> FirebaseDatabaseManager.updateLikes(database, firebaseSongId, info.getSongTitle(), value + 1 )
                }

                Snackbar.make(v, "Song ${info.getSongTitle()} has been added to favs", Snackbar.LENGTH_LONG).show()
            }
        }
    }
    override fun getItemCount(): Int {
        return songModelArrayList.size
    }

}