package com.example.mmplayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

data class NavigationItem(val label: String, val icon: ImageVector)

@Composable
fun NavigationBar(
    navController: NavController
){
    var selectedItem by remember { mutableStateOf("Music") }
    val items = listOf(
        NavigationItem("Video", Icons.Filled.PlayArrow),
        NavigationItem("Music", Icons.Filled.PlayArrow),
        NavigationItem("More", Icons.Filled.Menu)
    )

    Column(modifier = Modifier.fillMaxWidth(),) {
        NavigationBar {
            items.forEach {item ->
                val selected = selectedItem == item.label
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = selected,
                    onClick = {
                        selectedItem = item.label
                        navController.navigate(item.label)
                    }
                )
            }
        }
    }
}
