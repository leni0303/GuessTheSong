package com.example.cs306_coursework

import android.app.Activity
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

    private val foundLyricsKeyClassic = "com.example.myapp.FOUNDLYRICSCLASSIC"
    private val foundLyricsKeyCurrent = "com.example.myapp.FOUNDLYRICSCURRENT"

    private val foundSongsKey = "com.example.myapp.FOUNDSONGS"
    private val favouriteSongsKey = "com.example.myapp.FAVOURITESONGS"

    private val sp =
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)

    var mode: String = sp.getString(modeKey, "classic")!!
    var song:String = "nirvana(smells_like_teen_spirit).txt"
    var firstTimeUser: Boolean = sp.getBoolean(firstTimeKey, true)

    var foundLyricsClassic: MutableSet<String>
    var foundLyricsCurrent: MutableSet<String>

    var foundSongs: MutableSet<String>
    var favouriteSongs: MutableSet<String>


    init {
        foundLyricsClassic = HashSet(sp.getStringSet(foundLyricsKeyClassic, HashSet<String>()))
        foundLyricsCurrent = HashSet(sp.getStringSet(foundLyricsKeyCurrent, HashSet<String>()))

        foundSongs = HashSet(sp.getStringSet(foundSongsKey, HashSet<String>()))
        favouriteSongs = HashSet(sp.getStringSet(favouriteSongsKey, HashSet<String>()))

        song = if (mode == "classic") {
            sp.getString(classicKey, "nirvana(smells_like_teen_spirit).txt")!!
        } else {
            sp.getString(currentKey, "ed_sheeran_ft_stormzy(take_me_back_to_london).txt")!!
        }
    }

    fun showProgress(activity: Activity): MutableList<String> {
        //list of all lyrics
        val lyrics = SongDatabase.readSongLyricsAsList(activity, mode, song).toMutableList()

        if (mode == "classic") {
            for(x in 0 until lyrics.size) {
                if(!foundLyricsClassic.contains(lyrics[x])) {
                    lyrics[x] = "???"
                }
            }
        } else {
            for(x in 0 until lyrics.size) {
                if(!foundLyricsCurrent.contains(lyrics[x])) {
                    lyrics[x] = "???"
                }
            }
        }

        Log.d("TAG", "LYRICS $lyrics")
        return lyrics
    }

    fun saveLyric(lyric: String)
    {
        if(mode == "classic") {
            foundLyricsClassic.add(lyric)
            // save to preferences
            sp.edit().remove(foundLyricsKeyClassic).putStringSet(foundLyricsKeyClassic, foundLyricsClassic).apply()
        } else {
            foundLyricsCurrent.add(lyric)
            // save to preferences
            sp.edit().remove(foundLyricsKeyCurrent).putStringSet(foundLyricsKeyCurrent, foundLyricsCurrent).apply()
        }
    }

    fun clearLyrics()
    {
        if(mode == "classic") {
            foundLyricsClassic.clear()
            // save to preferences
            sp.edit().putStringSet(foundLyricsKeyClassic, foundLyricsClassic).apply()
        } else {
            foundLyricsCurrent.clear()
            // save to preferences
            sp.edit().putStringSet(foundLyricsKeyCurrent, foundLyricsCurrent).apply()
        }
    }

    fun removeLyricFromSet(set: HashSet<String>) {
        if(mode == "classic") {
            set.removeAll(foundLyricsClassic)
        } else {
            set.removeAll(foundLyricsCurrent)
        }
    }

    fun syncSong() {
        song = if (mode == "classic") {
            sp.getString(classicKey, "nirvana(smells_like_teen_spirit).txt")!!
        } else {
            sp.getString(currentKey, "ed_sheeran_ft_stormzy(take_me_back_to_london).txt")!!
        }
    }

    fun saveSong(song:String) {
        foundSongs.add(song)
        sp.edit().remove(foundSongsKey).putStringSet(foundSongsKey, foundSongs).apply()
    }

    fun saveFavouriteSong(song:String) {
        favouriteSongs.add(song)
        sp.edit().remove(favouriteSongsKey).putStringSet(favouriteSongsKey,favouriteSongs).apply()
    }

    fun removeFavouriteSong(song:String) {
        favouriteSongs.remove(song)
        sp.edit().remove(favouriteSongsKey).putStringSet(favouriteSongsKey,favouriteSongs).apply()
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
        Log.d("TAG", "app data mode $mode, song $song, first time $firstTimeUser,\n list of guessed classic songs $foundLyricsClassic,\n" +
                " list of guessed current songs $foundLyricsCurrent, \n found songs $foundSongs \n fav songs $favouriteSongs")
    }
}

