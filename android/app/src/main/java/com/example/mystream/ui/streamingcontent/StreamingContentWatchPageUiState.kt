package com.example.mystream.ui.streamingcontent

import androidx.compose.runtime.Immutable
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.ui.core.uimodel.StreamingContentIdUiModel
import com.example.mystream.ui.streamingcontent.uimodel.LiveChatMessageUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
sealed interface StreamingContentWatchPageUiState {

    data object Loading : StreamingContentWatchPageUiState

    @Immutable
    data class Loaded(
        val contentId: StreamingContentIdUiModel,
        val title: String,
        val messages: ImmutableList<LiveChatMessageUiModel>,
        val regexPatterns: ImmutableList<String>,
    ) : StreamingContentWatchPageUiState
}