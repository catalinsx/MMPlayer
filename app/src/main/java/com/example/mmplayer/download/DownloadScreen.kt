package com.example.mmplayer.download

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.mmplayer.player.Music
import com.example.mmplayer.player.MusicManager
import java.io.File
import kotlin.random.Random


// val directory = File(context.filesDir.absolutePath)
//    Column(){
//        Button(onClick = { directory.delete() }) {
//            Text(text = "Clear")
//        }
//    }

fun getRandomColor(): Color {
    val random = Random
    val red = random.nextInt(100, 256)
    val green = random.nextInt(100, 256)
    val blue = random.nextInt(100, 256)
    return Color(red, green, blue)
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerExo(context: Context) {
    var selectedChip by remember { mutableStateOf(setOf<String>()) }
    var downloadList by remember { mutableStateOf(MusicManager.getPlayList()) }


    fun filterSongsByGenres(songs: List<Music>): List<Music> {
        // if no music is selected, return all music
        return if (selectedChip.isEmpty()) {
            songs //
        } else {
            // filter the songs to include only those that have at least one of the selected genres
            songs.filter { song ->
                // check if the song has at least one of the selected genres
                selectedChip.any { genre -> song.genre.contains(genre, ignoreCase = true) }
            }
        }
    }

    var filteredSongs by remember { mutableStateOf(emptyList<Music>()) }

    LaunchedEffect(selectedChip) {
        filteredSongs = filterSongsByGenres(MusicManager.getPlayList())
    }

    // this block of code launches an effect that recomposes the composable when the downloadList changes
    // basically it triggers a recomposition
    // it is particularly useful for executing asynchronous situations
    LaunchedEffect(Unit) {
        downloadList = MusicManager.getPlayList()
    }

    Column {
        val genres = listOf("Rap", "Pop", "Hip-Hop", "House", "Rock")
        Row(modifier = Modifier.padding(8.dp)){
          genres.forEach { genre ->
              FilterChip(
                  selected = genre in selectedChip,
                  onClick = {
                      selectedChip = if (genre in selectedChip) { // if the genre is selected
                          selectedChip - genre // remove it from selected genres
                      } else {
                          selectedChip + genre // add it to selected genres
                      }

                      // update the filtered songs based on the selected genres
                      // call the filterSongsByGenres function to filter the songs
                      filteredSongs = filterSongsByGenres(downloadList)
                  },
                  label = { Text(genre) },
                  leadingIcon = {
                      if (genre in selectedChip) {
                          Icon(
                              imageVector = Icons.Default.Done,
                              contentDescription = "Selected",
                              modifier = Modifier.size(FilterChipDefaults.IconSize)
                          )
                      }
                  },
                  modifier = Modifier.padding(4.dp)
              )
          }
      }
        LazyColumn {
            items(filteredSongs) { song ->
                val randomColor = getRandomColor()
                val gradient = Brush.horizontalGradient(listOf(randomColor, randomColor))
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(gradient),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(modifier = Modifier.weight(1f)){
                            Text(
                                text = song.artist,
                                modifier = Modifier.padding(8.dp),
                                color = Color.Black
                            )
                            Text(
                                text = song.name,
                                modifier = Modifier.padding(8.dp),
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            IconButton(onClick = {
                              // inca nu i am dat de cap aici
                            }){
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "play/pause",
                                    tint = Color.Black
                                )
                            }
                            IconButton(onClick = {
                                val fileToDelete = File(context.filesDir, "${song.name} - ${song.artist}.mp3")
                                val deleted = fileToDelete.delete()

                                if (deleted) {
                                    Toast.makeText(context, "The music was succesfully deleted, refresh the page", Toast.LENGTH_SHORT).show()
                                    Log.d("DownloadScreen", "Fisierul ${fileToDelete.name} s a sters")
                                } else {
                                    Log.e("DownloadScreen", "Nu s-a sters, eroare")
                                }

                            }){
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "delete",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}