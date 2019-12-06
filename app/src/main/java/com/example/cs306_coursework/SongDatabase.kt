package com.example.cs306_coursework

import android.app.Activity

object SongDatabase {

    /**
     * put the song lyrics in a list
     */
    fun readSongLyricsAsList(activity: Activity, mode: String?, fileName: String?): List<String> {
        return activity.application.assets.open("$mode/$fileName").bufferedReader().readLines()
    }

    /**
     * get a random song for a mode
     */
    fun selectRandSong(activity: Activity, mode: String): String {
        return activity.application.assets.list(mode).random()
    }

    /**
     * parse song title and artist
     */
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