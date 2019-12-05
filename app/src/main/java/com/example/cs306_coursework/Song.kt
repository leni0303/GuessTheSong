package com.example.cs306_coursework

import com.google.firebase.database.Exclude

data class Song( val name:String? = "", val likes:Int? = 0) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "likes" to likes
        )
    }
}