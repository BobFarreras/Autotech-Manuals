package com.deixebledenkaito.autotechmanuals.ui.funcionsExternes


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun VideoThumbnail(
    thumbnailUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .clip(MaterialTheme.shapes.medium)
            .background(Color.DarkGray) // Fons sòlid mentre es carrega
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = "Miniatura del vídeo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Icona de reproducció
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Reproduir vídeo",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center),
            tint = Color.White
        )
    }
}