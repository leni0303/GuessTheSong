package com.example.cs306_coursework

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseManager {

    fun updateLikes(database: DatabaseReference, songId: String, songName:String, likes:Int) {
        // Write a message to the database
        val song = Song(songName, likes)
        database.child("songs").child(songId).setValue(song)
    }

    fun readLikes(database:DatabaseReference, songId: String, songName: String, onLikes: (Int)->Any) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                var value = dataSnapshot.child("songs").child(songId).getValue(Song::class.java)
                if (value == null)
                {
                    value = Song(songName, 1)
                    database.child("songs").child(songId).setValue(value)
                }
                onLikes(value.likes!!)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }


}