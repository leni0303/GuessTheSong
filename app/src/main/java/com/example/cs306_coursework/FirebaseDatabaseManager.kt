package com.example.cs306_coursework

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseManager {

    data class Song( val name:String? = "", val likes:Int? = 0)

    /**
     * update likes counter in the db
     */
    private fun updateLikes(database: DatabaseReference, foundSongsModel: FoundSongsListModel, likes:Int) {
        // Write a message to the database
        database.child("songs").child(foundSongsModel.firebaseId()).setValue(Song(foundSongsModel.title, likes))
    }

    /**
     * get likes for a song from the db
     */
    private fun readLikes(database:DatabaseReference, foundSongsModel: FoundSongsListModel, onLikes: (Int)->Any) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                val fbId = foundSongsModel.firebaseId()

                var value = dataSnapshot.child("songs").child(fbId).getValue(Song::class.java)
                if (value == null)
                {
                    value = Song(foundSongsModel.title, 1)
                    database.child("songs").child(fbId).setValue(value)
                }
                onLikes(value.likes!!)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * add a like
     */
    fun addLike(database: DatabaseReference, foundSongsModel: FoundSongsListModel)
    {
        readLikes(database, foundSongsModel) { value -> updateLikes(database, foundSongsModel, value + 1 ) }
    }

    /**
     * remove a like
     */
    fun removeLike(database: DatabaseReference, foundSongsModel: FoundSongsListModel)
    {
        readLikes(database, foundSongsModel) { value -> updateLikes(database, foundSongsModel, value - 1 ) }
    }

    /**
     * get a list with all of the songs that have been favourited by players
     */
    fun getMostPopular(database: DatabaseReference, onSongs: (List<Song>)->Any)
    {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val songs: MutableList<Song> = mutableListOf()
                for (s in dataSnapshot.child("songs").children) {
                    songs.add(s.getValue(Song::class.java)!!)
                }
                onSongs(songs)//.sortedWith(compareBy({ it.likes })))
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }
}