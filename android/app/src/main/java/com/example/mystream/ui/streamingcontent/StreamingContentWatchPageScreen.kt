package com.example.mystream.ui.streamingcontent

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StreamingContentWatchPageScreen(
    viewModel: StreamingContentWatchPageViewModel,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Streaming Content Watch Page",
        modifier = modifier,
    )
}