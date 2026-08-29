package com.example.mystream.ui.streamingcontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mystream.ui.streamingcontent.uimodel.LiveChatMessageUiModel
import kotlinx.collections.immutable.ImmutableList

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
        ChatList(
            messages = uiState.messages,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ChatList(
    messages: ImmutableList<LiveChatMessageUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(messages) { message ->
            ChatItem(message = message)
        }
    }
}

@Composable
private fun ChatItem(
    message: LiveChatMessageUiModel,
) {
    Card {
        Text(
            text = "${message.author}: ${message.message}",
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        )
    }
}