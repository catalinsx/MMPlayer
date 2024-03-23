package com.example.mmplayer

import android.media.AudioMetadata
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mmplayer.more.MoreScreen
import com.example.mmplayer.music.MusicManager
import com.example.mmplayer.music.SongScreen
import com.example.mmplayer.ui.theme.MMPlayerTheme
import com.example.mmplayer.video.VideoPlayerExo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.Base64


val serverAddress = "192.168.0.189" // iti pui ipu tau aici
val serverPort = 7777
lateinit var socket: Socket
var mediaPlayer: MediaPlayer = MediaPlayer()
var songList = mutableListOf<String>()

class MainActivity : ComponentActivity() {

    private lateinit var player: ExoPlayer

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = ExoPlayer.Builder(this).build()
        val playList = MusicManager.getPlayList()
        val packageName = packageName
        val videoUrl =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val mmr = MediaMetadataRetriever()


        setContent {

            MMPlayerTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    val navController = rememberNavController()
                    Column {
                        NavHost(
                            navController = navController,
                            startDestination = "More",
                            modifier = Modifier.weight(1f)
                        ) {
                            composable("Video") {
                                VideoPlayerExo(videoUrl = videoUrl)
                            }
                            composable("Music") {

                                MusicManager.deletePlayList()
                                val directory = File(applicationContext.filesDir.absolutePath)
                                directory.listFiles()?.forEach {
                                    println(it.name)
                                    if (it.name.endsWith(".mp3")) {
                                        println("File found")

                                        mmr.setDataSource(this@MainActivity, it.toUri())


                                        val title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                        val artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                                        val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                        val cover = mmr.embeddedPicture

                                        Log.d("MP3", "title=$title, artist=$artist, duration=$duration")

                                        MusicManager.addMusic(
                                            artist = artist ?: "Unknown",
                                            name = title ?: "Unknown",
                                            music = it.path,
                                            cover = 0

                                        )
                                    }
                                }
                                SongScreen(
                                    playList = MusicManager.getPlayList(),
                                    player = player
                                )
                            }
                            composable("More") {
                                //songList.clear()
                                sendMessageToServer("<LIST>\r\n")
                                connectToServer()
                                MoreScreen()
                            }
                        }
                        NavigationBar(navController = navController)
                    }
                }

            }
            connectToServer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun connectToServer() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Connect to the server
                socket = Socket(serverAddress, serverPort)
                println("Connected to server.")
                listenForData()
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun listenForData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                var response: String
                var songString: String = ""

                while (input.readLine().also { response = it } != null) {

                    if(response.startsWith("<PLAY>")) {
                        //daca cotine <EOS> stim ca s-a terminat de trimis, si facem pasii inversi facuti in server
                        // anume stringu devien un byte array
                        // si byte arrayul devine fisier
                        // ulterior se apeleaza playSong care da play melodiei
                        // cateva remarci:
                        // playSong pune sigur de la el .mp3, sa nu te trezesti sa pui si tu playSog("ceva.mp3") ca o sa te trezesti cu eroare
                        // oricum melodiile trimise de server in LIST sunt fara .mp3 deci n ai avea dc sa ai probleme
                        // nici numele la fisier song.mp3 si song di saveByte... si playSong n ar tb schimbate in mod special pt ca
                        // o sa si dea override cand vine o piesa noua
                        // in schimb daca vrei sa pastrezi piesele anterioare in telefon, ca sa nu mai stai sa le descarci de pe server
                        // poti sa ti faci tu metoda sa verifici daca exista fisieru in internal storage, caz in care nu mai descarci si dai daor play
                        // numa ca tre sa ti faci si cv variabila globala cu numele la melodie, nu cum e acu song.mp3, da nu cred ca are rost, tu stii
                        if(response.contains("<EOS>")) {
                            val byteArray = Base64.getDecoder().decode(songString)
                            saveByteArrayToFile(byteArray, "song.mp3")

                          //  playSong("song")
                            songString = ""
                            connectToServer() //temporar
                        } else {
                            var tmpString: String
                            tmpString = response.replace("<PLAY>", "")
                            songString += tmpString
                            println("Received from server: $tmpString")
                        }




                    }
                    else if(response.startsWith("<LIST>") && response.endsWith("<EOS>")) {
                        var tmpString: String = response.replace("<LIST>", "").replace("<EOS>", "")
                        val strSongs = tmpString.split(";")

                        for(song in strSongs) {
                            songList.add(song)
                            println(song)
                        }

                        connectToServer() //temporar
                    }

                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    fun saveByteArrayToFile(byteArray: ByteArray, fileName: String) {
        try {
            // Open a file output stream in private mode
            val outputStream: FileOutputStream = openFileOutput(fileName, MODE_PRIVATE)

            // Write the byte array to the file
            outputStream.write(byteArray)

            // Close the output stream
            outputStream.close()

            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(this, File(applicationContext.filesDir.absolutePath + "/song.mp3").toUri())

            val title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            Log.d("MP3", "title=$title, artist=$artist, duration=$duration")

            val currentFile = File(applicationContext.filesDir.absolutePath + "/song.mp3")
            val newFile = File(applicationContext.filesDir.absolutePath + "/$title - $artist.mp3")
            val succes = currentFile.renameTo(newFile)

            if(succes) {
                Log.d("MP3", "File renamed to $title - $artist.mp3")
            } else {
                Log.d("MP3", "File not renamed")
            }

            // Optionally, you can notify the user that the file has been saved successfully
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle errors, such as permission issues or IO exceptions
        }
    }

}

fun sendMessageToServer(message: String) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            // verificam ca socketu sa nu fie null, si sa fie conectat
            if (::socket.isInitialized && socket.isConnected) {
                val out = PrintWriter(socket.getOutputStream(), true)

                // trimitem mesaju
                out.println(message)
                println("Message sent to server: $message")
            } else {
                println("Socket is not initialized or not connected.")
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }


}
