package com.example.cs306_coursework

data class FoundSongsListModel (
    var id: String = "",
    var title: String = "",
    var artist: String = "",
    var image_status_drawable: Int = 0
)
{
    /**
     * remove text extension
     */
    fun firebaseId(): String
    {
        return id.removeSuffix(".txt")
    }
}