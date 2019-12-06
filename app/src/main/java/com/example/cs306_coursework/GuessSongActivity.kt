package com.example.cs306_coursework

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class GuessSongActivity : AppCompatActivity() {

    private var ad:AppData? = null
    private var artistAndTitle: Pair<CharSequence, CharSequence>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess_song)

        ad = AppData.getInstance(this)

        // get artist and title
        artistAndTitle = SongDatabase.readArtistAndSong(ad!!.song)

        val lyricsText = findViewById<TextView>(R.id.lyricsText)

        // save a text file of found and unknown lyrics
        val lyricsList = ad!!.showProgress(this)
        for (x in lyricsList) {
            // add each lyric item
            lyricsText.append(x + "\n")
        }

        // make btn unavailable if user hasn't collected a lyric
        val guessBtn = findViewById<MaterialButton>(R.id.guessBtn)
        // if there are no collected lyrics
        if(ad!!.mode == "classic" && ad!!.foundLyricsClassic.isEmpty()) {
            Toast.makeText(this, R.string.no_lyric, Toast.LENGTH_LONG)
                .show()
            //disable button
            guessBtn.isClickable = false
        } else if (ad!!.mode == "current" && ad!!.foundLyricsCurrent.isEmpty()) {
            Toast.makeText(this, R.string.no_lyric, Toast.LENGTH_LONG)
                .show()
            // disable button
            guessBtn.isClickable = false
        }
    }

    /**
     * get guess and compare to actual song
     */
    fun guessSong(view: View) {
        // get guesses for title and song
        val guessTitle = findViewById<TextInputEditText>(R.id.titleInputText).text
        val guessArtist = findViewById<TextInputEditText>(R.id.artistInputText).text

        if(guessTitle!!.contains(artistAndTitle!!.second, ignoreCase = true) && guessArtist!!.contains(artistAndTitle!!.first, ignoreCase = true)) {
            Toast.makeText(this, R.string.congrats, Toast.LENGTH_LONG)
                .show()

            // add song to found song list
            ad!!.saveSong(ad!!.song)
            ad!!.debug()

            // spawn a new song
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("songStatus", "guessed")
            // go back to main activity
            this.startActivity(intent)
        } else {
            Toast.makeText(this, R.string.guess_again, Toast.LENGTH_LONG)
                .show()
        }
    }
}
