package com.example.mmplayer.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mmplayer.sendMessageToServer
import com.example.mmplayer.songList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

fun getRandomColor(): Color {
    val random = Random
    val red = random.nextInt(100, 256)
    val green = random.nextInt(100, 256)
    val blue = random.nextInt(100, 256)
    return Color(red, green, blue)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(){
    var updater by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        fun stopProgress() {
            // launch a coroutine on main dispatcher
            // a coroutine is a powerful way to handle async tasks
            // main dispatcher is the main thread used for ui updates
            CoroutineScope(Dispatchers.Main).launch {
                delay(15000)
                updater = false
            }
        }
        if(updater)
            LinearProgressIndicator(modifier =  Modifier.fillMaxWidth())
        LazyColumn {
            items(songList) { song ->
                val startColor = getRandomColor()
                val endColor = getRandomColor()
                val gradient = Brush.horizontalGradient(listOf(startColor, endColor))
                ElevatedCard(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        sendMessageToServer("<PLAY>${song.music}<EOS>\r\n")
                        updater = true
                        stopProgress()
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .background(brush = gradient)
                            .fillMaxSize()
                    ) {
                        Text(
                            text =  "${song.artist} - ${song.name}",
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                            color = Color.Black
                        )
                        Text(
                            text = "Album: ${song.album}",
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                            color = Color.Black
                        )
                        Text(
                            text = "Genre: ${song.genre}",
                            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                            color = Color.Black
                        )
                     }

                    }
                }
            }
        }
    }
