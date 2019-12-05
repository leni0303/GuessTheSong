package com.example.cs306_coursework

class SongListModel {
    var title: String? = null
    var artist: String? = null
    var id: String? = null
    var image_status_drawable: Int = 0

    fun getSongTitle(): String {
        return title.toString()
    }

    fun setSongTitle(title: String) {
        this.title = title
    }

    fun getSongArtist(): String {
        return artist.toString()
    }

    fun setSongArtist(artist: String) {
        this.artist = artist
    }

    fun getSongId(): String {
        return id.toString()
    }

    fun setSongid(id: String) {
        this.id = id
    }

    fun setImage_drawables(image_drawable: Int) {
        this.image_status_drawable = image_drawable
    }

    fun getImage_drawables(): Int {
        return image_status_drawable
    }
}