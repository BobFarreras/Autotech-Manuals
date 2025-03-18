package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes.videoPlayer


import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = false, // Nova propietat per controlar el mode pantalla completa
    autoPlay: Boolean = false // Nova propietat per reproduir automàticament
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    // Configura el reproductor per a mode pantalla completa
    if (isFullScreen) {
        exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    }

    // Reprodueix automàticament si autoPlay és true
    LaunchedEffect(autoPlay) {
        if (autoPlay) {
            exoPlayer.play()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT // Ajusta el vídeo a la pantalla
            }
        },
        modifier = if (isFullScreen) {
            Modifier.fillMaxSize() // Ocupa tota la pantalla en mode pantalla completa
        } else {
            modifier
                .fillMaxWidth()
                .height(200.dp) // Alçada per defecte quan no està en mode pantalla completa
        }
    )
}