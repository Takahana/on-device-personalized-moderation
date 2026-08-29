package com.example.mystream.ui.streamingcontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StreamingContentWatchPageScreen(
    viewModel: StreamingContentWatchPageViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Streaming Content Watch Page",
        )

        val uiState by viewModel.uiState.collectAsState()
        uiState.messages.forEach { message ->
            Card {
                Text(
                    text = "${message.author}: ${message.message}",
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                )
            }
        }
    }
}