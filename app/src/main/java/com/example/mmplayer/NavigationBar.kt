package com.example.mmplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class NavigationItem(val label: String, val icon: ImageVector)

@Composable
fun NavigationBar(
    navController: NavController
){
    var selectedItem by remember { mutableStateOf("Music") }
    val items = listOf(
        NavigationItem("Downloaded", rememberDownload()),
        NavigationItem("Player", rememberPlayArrow()),
        NavigationItem("Music", rememberLibraryMusic())
    )


    Column(modifier = Modifier.fillMaxWidth()) {
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

@Composable
fun rememberDownload(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "download",
            defaultWidth = 27.5.dp,
            defaultHeight = 27.5.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 26.25f)
                quadToRelative(-0.25f, 0f, -0.479f, -0.083f)
                quadToRelative(-0.229f, -0.084f, -0.438f, -0.292f)
                lineToRelative(-6.041f, -6.083f)
                quadToRelative(-0.417f, -0.375f, -0.396f, -0.917f)
                quadToRelative(0.021f, -0.542f, 0.396f, -0.917f)
                reflectiveQuadToRelative(0.916f, -0.396f)
                quadToRelative(0.542f, -0.02f, 0.959f, 0.396f)
                lineToRelative(3.791f, 3.792f)
                verticalLineTo(8.292f)
                quadToRelative(0f, -0.584f, 0.375f, -0.959f)
                reflectiveQuadTo(20f, 6.958f)
                quadToRelative(0.542f, 0f, 0.938f, 0.375f)
                quadToRelative(0.395f, 0.375f, 0.395f, 0.959f)
                verticalLineTo(21.75f)
                lineToRelative(3.792f, -3.792f)
                quadToRelative(0.375f, -0.416f, 0.917f, -0.396f)
                quadToRelative(0.541f, 0.021f, 0.958f, 0.396f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.917f)
                reflectiveQuadToRelative(-0.375f, 0.958f)
                lineToRelative(-6.083f, 6.042f)
                quadToRelative(-0.209f, 0.208f, -0.438f, 0.292f)
                quadToRelative(-0.229f, 0.083f, -0.479f, 0.083f)
                close()
                moveTo(9.542f, 32.958f)
                quadToRelative(-1.042f, 0f, -1.834f, -0.791f)
                quadToRelative(-0.791f, -0.792f, -0.791f, -1.834f)
                verticalLineToRelative(-4.291f)
                quadToRelative(0f, -0.542f, 0.395f, -0.938f)
                quadToRelative(0.396f, -0.396f, 0.938f, -0.396f)
                quadToRelative(0.542f, 0f, 0.917f, 0.396f)
                reflectiveQuadToRelative(0.375f, 0.938f)
                verticalLineToRelative(4.291f)
                horizontalLineToRelative(20.916f)
                verticalLineToRelative(-4.291f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                quadToRelative(0.583f, 0f, 0.958f, 0.396f)
                reflectiveQuadToRelative(0.375f, 0.938f)
                verticalLineToRelative(4.291f)
                quadToRelative(0f, 1.042f, -0.791f, 1.834f)
                quadToRelative(-0.792f, 0.791f, -1.834f, 0.791f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberPlayArrow(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "play_arrow",
            defaultWidth = 27.5.dp,
            defaultHeight = 27.5.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15.542f, 30f)
                quadToRelative(-0.667f, 0.458f, -1.334f, 0.062f)
                quadToRelative(-0.666f, -0.395f, -0.666f, -1.187f)
                verticalLineTo(10.917f)
                quadToRelative(0f, -0.75f, 0.666f, -1.146f)
                quadToRelative(0.667f, -0.396f, 1.334f, 0.062f)
                lineToRelative(14.083f, 9f)
                quadToRelative(0.583f, 0.375f, 0.583f, 1.084f)
                quadToRelative(0f, 0.708f, -0.583f, 1.083f)
                close()
                moveToRelative(0.625f, -10.083f)
                close()
                moveToRelative(0f, 6.541f)
                lineToRelative(10.291f, -6.541f)
                lineToRelative(-10.291f, -6.542f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberLibraryMusic(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "library_music",
            defaultWidth = 27.5.dp,
            defaultHeight = 27.5.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20.375f, 25.75f)
                quadToRelative(1.667f, 0f, 2.854f, -1.167f)
                quadToRelative(1.188f, -1.166f, 1.188f, -2.833f)
                verticalLineToRelative(-10f)
                horizontalLineToRelative(3.5f)
                quadToRelative(0.541f, 0f, 0.916f, -0.396f)
                reflectiveQuadToRelative(0.375f, -0.979f)
                quadToRelative(0f, -0.542f, -0.375f, -0.937f)
                quadToRelative(-0.375f, -0.396f, -0.958f, -0.396f)
                horizontalLineToRelative(-3.792f)
                quadToRelative(-0.583f, 0f, -0.937f, 0.375f)
                quadToRelative(-0.354f, 0.375f, -0.354f, 0.958f)
                verticalLineToRelative(8.25f)
                quadToRelative(-0.5f, -0.375f, -1.125f, -0.583f)
                quadToRelative(-0.625f, -0.209f, -1.292f, -0.209f)
                quadToRelative(-1.667f, 0f, -2.792f, 1.146f)
                quadToRelative(-1.125f, 1.146f, -1.125f, 2.771f)
                reflectiveQuadToRelative(1.125f, 2.812f)
                quadToRelative(1.125f, 1.188f, 2.792f, 1.188f)
                close()
                moveToRelative(-8.833f, 5.375f)
                quadToRelative(-1.084f, 0f, -1.875f, -0.792f)
                quadToRelative(-0.792f, -0.791f, -0.792f, -1.875f)
                verticalLineTo(6.25f)
                quadToRelative(0f, -1.083f, 0.792f, -1.854f)
                quadToRelative(0.791f, -0.771f, 1.875f, -0.771f)
                horizontalLineTo(33.75f)
                quadToRelative(1.083f, 0f, 1.854f, 0.771f)
                quadToRelative(0.771f, 0.771f, 0.771f, 1.854f)
                verticalLineToRelative(22.208f)
                quadToRelative(0f, 1.084f, -0.771f, 1.875f)
                quadToRelative(-0.771f, 0.792f, -1.854f, 0.792f)
                close()
                moveToRelative(0f, -2.667f)
                horizontalLineTo(33.75f)
                verticalLineTo(6.25f)
                horizontalLineTo(11.542f)
                verticalLineToRelative(22.208f)
                close()
                moveTo(6.25f, 36.375f)
                quadToRelative(-1.083f, 0f, -1.854f, -0.771f)
                quadToRelative(-0.771f, -0.771f, -0.771f, -1.854f)
                verticalLineTo(10.167f)
                quadToRelative(0f, -0.542f, 0.375f, -0.917f)
                reflectiveQuadToRelative(0.958f, -0.375f)
                quadToRelative(0.542f, 0f, 0.917f, 0.396f)
                reflectiveQuadToRelative(0.375f, 0.937f)
                verticalLineTo(33.75f)
                horizontalLineToRelative(23.583f)
                quadToRelative(0.542f, 0f, 0.917f, 0.375f)
                reflectiveQuadToRelative(0.375f, 0.958f)
                quadToRelative(0f, 0.542f, -0.396f, 0.917f)
                reflectiveQuadToRelative(-0.937f, 0.375f)
                close()
                moveTo(11.542f, 6.25f)
                verticalLineToRelative(22.208f)
                verticalLineTo(6.25f)
                close()
            }
        }.build()
    }
}