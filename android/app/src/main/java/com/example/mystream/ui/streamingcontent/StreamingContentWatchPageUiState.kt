package com.example.mystream.ui.streamingcontent

import androidx.compose.runtime.Immutable
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.ui.streamingcontent.uimodel.LiveChatMessageUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class StreamingContentWatchPageUiState(
    val messages: ImmutableList<LiveChatMessageUiModel> = persistentListOf(),
)