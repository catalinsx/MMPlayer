package com.example.mmplayer.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mmplayer.sendMessageToServer
import com.example.mmplayer.songList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(){
    Column(
        modifier = Modifier.background(Color.Blue).fillMaxSize()
    ) {
        LazyColumn {
            items(songList) { song ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        sendMessageToServer("<PLAY>$song<EOS>\r\n")
                    }
                ) {
                    Text(
                        text = song
                    )
                }
            }
        }
    }
}