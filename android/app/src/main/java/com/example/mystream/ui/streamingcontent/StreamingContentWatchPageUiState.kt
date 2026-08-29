package com.example.mystream.ui.streamingcontent

import com.example.mystream.domain.chat.LiveChatMessage

data class StreamingContentWatchPageUiState(
    val messages: List<LiveChatMessage> = emptyList(),
)