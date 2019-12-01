package com.example.cs306_coursework

import android.content.Context
import android.content.Intent
import android.util.Log

class AppData constructor(private val context: Context) {

    // invoke singleton
    companion object: SingletonHolder<AppData, Context>(::AppData)

    private val spName = "com.example.myapp.PREF"
    private val modeKey = "com.example.myapp.MODE"
    private val classicKey ="com.example.myapp.CLASSICSONG"
    private val currentKey = "com.example.myapp.CURRENTSONG"
    private val firstTimeKey = "com.example.myapp.ISFIRSTTIME"
    private val foundLyricsKey = "com.example.myapp.FOUNDLYRICS"

    private val sp =
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)

    var mode: String = sp.getString(modeKey, "classic")!!
    var song:String = "nirvana(smells_like_teen_spirit).txt"
    var firstTimeUser: Boolean = sp.getBoolean(firstTimeKey, true)

    var foundLyrics: MutableSet<String>

    init {
        foundLyrics = sp.getStringSet("a", HashSet<String>())!!

        song = if (mode == "classic") {
            sp.getString(classicKey, "nirvana(smells_like_teen_spirit).txt")!!
        } else {
            sp.getString(currentKey, "ed_sheeran_ft_stormzy(take_me_back_to_london).txt")!!
        }
    }

    /**
     * loop each lyric
     * if !found replace with ?
     */

    // list of guessed lyrics
    // method save/add lyric
    // clear lyrics - gussed lyric or change song
    // get lyrics - get all lyrics
    fun saveLyric(lyric: String)
    {
        foundLyrics.add(lyric)
        //save to preferences
        sp.edit().putStringSet(foundLyricsKey, foundLyrics).apply()
    }

    fun clearLyrics()
    {
        foundLyrics.clear()
        sp.edit().putStringSet(foundLyricsKey, foundLyrics).apply()
    }

    fun syncSong() {
        song = if (mode == "classic") {
            sp.getString(classicKey, "nirvana(smells_like_teen_spirit).txt")!!
        } else {
            sp.getString(currentKey, "ed_sheeran_ft_stormzy(take_me_back_to_london).txt")!!
        }
    }

    fun savePrefModeAndSong(modeName: String, songName: String) {
        // update mode and song params
        mode = modeName
        song = songName

        // update sp
        val editor = sp.edit()
        // write mode in sp
        editor.putString(modeKey, modeName)

        // write song in sp
        if (modeName == "classic") {
            editor.putString(classicKey, songName)
        } else {
            editor.putString(currentKey, songName)
        }
        // save mode and song
        editor.apply()
    }

    fun savePrefMode(modeName: String) {
        // update mode param
        mode = modeName

        // update sp
        val editor = sp.edit()
        // write mode in sp
        editor.putString(modeKey, modeName).apply()
    }

    fun savePrefSong(songName: String) {
        // update song param
        song = songName

        // update sp
        val editor = sp.edit()
        // write mode in sp
        if (mode == "classic") {
            editor.putString(classicKey, songName).apply()
        } else {
            editor.putString(currentKey, songName).apply()
        }
    }

    fun savePrefFirstTime() {
        firstTimeUser = false
        val editor = sp.edit()
        editor.putBoolean(firstTimeKey, false).apply()
    }

    fun checkFirstTimeUser(intent: Intent) {
        if (firstTimeUser) {
            context.startActivity(intent)
        }
    }

    fun debug() {
        Log.d("TAG", "app data mode $mode, song $song, first time $firstTimeUser, list of guessed songs ${foundLyrics}")
    }
}

