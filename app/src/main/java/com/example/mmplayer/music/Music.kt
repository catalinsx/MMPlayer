package com.example.mmplayer.music

import com.example.mmplayer.MainActivity
import com.example.mmplayer.R

data class Music(
    val artist: String,
    val name: String,
    val music: String,
    val cover: Int
)



object MusicManager {
    private val playList = mutableListOf<Music>()

    fun getPlayList(): List<Music> {
        return playList.toList()
    }

    fun addMusic( artist: String, name: String,  music: String,  cover: Int){
        val newMusic = Music(artist, name, music, cover)
        playList.add(newMusic)
    }
    fun deletePlayList(){
        playList.clear()
    }
}