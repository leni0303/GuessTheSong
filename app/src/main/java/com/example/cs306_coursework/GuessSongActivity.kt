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

        val lyricsList = ad!!.showProgress(this)
        for (x in lyricsList) {
            lyricsText.append(x + "\n")
        }

        // make btn unavailable if user hasn't collected a lyric
        val guessBtn = findViewById<MaterialButton>(R.id.guessBtn)
        if(ad!!.mode == "classic" && ad!!.foundLyricsClassic.isEmpty()) {
            guessBtn.isClickable = false
            Toast.makeText(this, "Try and collect a lyric first", Toast.LENGTH_LONG)
                .show()
        } else if (ad!!.mode == "current" && ad!!.foundLyricsCurrent.isEmpty()) {
            //guessBtn.setBackgroundColor(getColor(R.color.lightGray))
            guessBtn.isClickable = false

            Toast.makeText(this, "Try and collect a lyric first", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun guessSong(view: View) {
        val guessTitle = findViewById<TextInputEditText>(R.id.titleInputText).text
        val guessArtist = findViewById<TextInputEditText>(R.id.artistInputText).text

        Log.d("TAG", "guessed title $guessTitle, guess artist $guessArtist, \n " +
                "actual song ${artistAndTitle!!.second}, actual artist ${artistAndTitle!!.first} ")

        //if (artistAndTitle!!.first == guessArtist && artistAndTitle!!.second == guessTitle) {
        if(guessTitle!!.contains(artistAndTitle!!.second, ignoreCase = true) && guessArtist!!.contains(artistAndTitle!!.first, ignoreCase = true)) {
            Toast.makeText(this, "Congrats you guessed the song!", Toast.LENGTH_LONG)
                .show()

            // add song to list
            ad!!.saveSong(ad!!.song)
            ad!!.debug()

            // TODO: spawn new song
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("songStatus", "guessed")
           // this.startActivity(Intent(this, MapsActivity::class.java))
            this.startActivity(intent)
        } else {
            Toast.makeText(this, "Guess again", Toast.LENGTH_LONG)
                .show()
        }
    }
}
