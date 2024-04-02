package com.example.mmplayer.player

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.mmplayer.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun SongScreen(playList: MutableList<Music>, player: ExoPlayer){

    val pagerState = rememberPagerState(pageCount = { playList.count() })
    val playingSongIndex = remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(pagerState.currentPage) {
        playingSongIndex.intValue = pagerState.currentPage
        player.seekTo(pagerState.currentPage, 0)
    }

    LaunchedEffect(player.currentMediaItemIndex) {
        playingSongIndex.intValue = player.currentMediaItemIndex
        pagerState.animateScrollToPage(
            playingSongIndex.intValue,
            animationSpec = tween(500)
        )
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        playList.forEach {
            val path =  it.music
                //"android.resource://" + packageName + "/" + it.music
            val mediaItem = MediaItem.fromUri(Uri.parse(path))
            player.addMediaItem(mediaItem)
        }
    }
    player.prepare()


    val isPlaying = remember {
        mutableStateOf(false)
    }

    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }


    LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
        delay(1000)
        currentPosition.longValue = player.currentPosition
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }

    LaunchedEffect(player.duration) {
        if (player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }

     val colorBg1 = 0xFF08203E
     val colorBg2 = 0xFF557C93

    val gradient = Brush.linearGradient(
        colors = listOf(Color(colorBg1), Color(colorBg2)),
        start = Offset(1000f, -1000f),
        end = Offset(1000f, 1000f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
        , contentAlignment = Alignment.Center

    ) {
        val configuration = LocalConfiguration.current

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            IconButton(onClick = {
                if (playingSongIndex.intValue < playList.size) {
                    playList.removeAt(playingSongIndex.intValue)
                    player.removeMediaItem(playingSongIndex.intValue)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "delete song"
                )
            }



            /***
             * Animated texts includes song name and its artist
             * Animates when the song is switching
             */
            AnimatedContent(targetState = playingSongIndex.intValue, transitionSpec = {
                (scaleIn() + fadeIn()) with (scaleOut() + fadeOut())
            }, label = "") {
                Text(
                        text = playList[it].name, fontSize = 24.sp,
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.ExtraBold)
                    )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedContent(targetState = playingSongIndex.intValue, transitionSpec = {
                (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
            }, label = "") {
                Text(
                    text = playList[it].artist, fontSize = 12.sp, color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            /***
             * Includes animated song album cover
             */
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState,
                pageSize = PageSize.Fixed((configuration.screenWidthDp / (1.7)).dp),
                contentPadding = PaddingValues(horizontal = 85.dp)
            ) { page ->
                val painter = painterResource(id = R.drawable.itunes)
                
                if (page == pagerState.currentPage) {
                    VinylAlbumCoverAnimation(isSongPlaying = isPlaying.value, painter = painter)
                } else {
                    VinylAlbumCoverAnimation(isSongPlaying = false, painter = painter)
                }
            }
            Spacer(modifier = Modifier.height(54.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {

                TrackSlider(
                    value = sliderPosition.longValue.toFloat(),
                    onValueChange = {
                        sliderPosition.longValue = it.toLong()
                    },
                    onValueChangeFinished = {
                        currentPosition.longValue = sliderPosition.longValue
                        player.seekTo(sliderPosition.longValue)
                    },
                    songDuration = totalDuration.longValue.toFloat()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    Text(
                        text = (currentPosition.longValue).convertToText(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )

                    val remainTime = totalDuration.longValue - currentPosition.longValue
                    Text(
                        text = if (remainTime >= 0) remainTime.convertToText() else "",
                        modifier = Modifier
                            .padding(8.dp),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton(icon = R.drawable.ic_previous, size = 40.dp, onClick = {
                    player.seekToPreviousMediaItem()
                })
                Spacer(modifier = Modifier.width(20.dp))
                ControlButton(
                    icon = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play,
                    size = 100.dp,
                    onClick = {
                        if (isPlaying.value) {
                            player.pause()
                        } else {
                            player.play()
                        }
                        isPlaying.value = player.isPlaying
                    })
                Spacer(modifier = Modifier.width(20.dp))
                ControlButton(icon = R.drawable.ic_next, size = 40.dp, onClick = {
                    player.seekToNextMediaItem()
                })
            }
        }
    }
}