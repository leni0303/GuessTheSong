package com.example.cs306_coursework

import android.app.Activity

object SongDatabase {

    // read a specific song from specific mode from assets
    fun readSongLyrics(activity: Activity, mode: String, fileName: String): String {
        return activity.application.assets.open("$mode/$fileName").bufferedReader().use { it.readText()}
    }

    fun readSongLyricsAsList(activity: Activity, mode: String?, fileName: String?): List<String> {
        return activity.application.assets.open("$mode/$fileName").bufferedReader().readLines()
    }

    /* private fun saveSongInList() {
         val sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
         val currentMode = sharedpreferences.getString("com.example.myapp.MODE", null)
         val currentSong = sharedpreferences.getString("com.example.myapp.SONG", null)

         Log.d("TAG", "mode $currentMode, song $currentSong")
         songLyricsList = readSongLyricsAsList(currentMode, currentSong)
     }*/

    fun selectRandSong(activity: Activity, mode: String): String {
        return activity.application.assets.list(mode).random()
    }

    fun readArtistAndSong(songName: String): Pair<CharSequence, CharSequence> {

        val start = songName.indexOf("(", 0, true)
        val end = songName.indexOf(")", start, true)

        val artistSequence = songName.subSequence(0, start)
        val titleSequence = songName.subSequence(start+1, end)

        val artist = artistSequence.replace(Regex("_"), " ")
        val title = titleSequence.replace(Regex("_"), " ")

        return Pair(artist, title)
    }
}