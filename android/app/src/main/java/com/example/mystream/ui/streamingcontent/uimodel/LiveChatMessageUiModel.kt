package com.example.mystream.ui.streamingcontent.uimodel

import androidx.compose.runtime.Immutable
import com.example.mystream.service.FilteredLiveChatMessage.HiddenMessage.Reason

@Immutable
sealed interface LiveChatMessageUiModel {

    @Immutable
    data class ShowMessage(
        val author: String,
        val message: String,
    ) : LiveChatMessageUiModel

    @Immutable
    data class HiddenMessage(
        val author: String,
        val message: String,
        val reason: Reason,
    ) : LiveChatMessageUiModel
}