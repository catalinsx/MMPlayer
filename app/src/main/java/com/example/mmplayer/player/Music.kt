package com.example.mmplayer.player

data class Music(
    val artist: String,
    val name: String,
    val music: String?,
    val cover: Int? = null,
    val album: String,
    val genre: String
)

object MusicManager {
    private var playList: MutableList<Music> = mutableListOf()
    fun getPlayList(): MutableList<Music> {
        return playList
    }

    fun addMusic(
        artist: String,
        name: String,
        music: String,
        cover: Int? = null,
        album: String,
        genre: String){
        val newMusic = Music(artist, name, music, cover, album, genre)
        playList.add(newMusic)
    }
    fun deletePlayList(){
        playList.clear()
    }
}