package com.example.mystream.ui.streamingcontent.uimodel

import com.example.mystream.service.FilteredLiveChatMessage

fun List<FilteredLiveChatMessage>.mapToUiModel(): List<LiveChatMessageUiModel> {
  return map { it.toUiModel() }
}

fun FilteredLiveChatMessage.toUiModel(): LiveChatMessageUiModel {
    return when (this) {
        is FilteredLiveChatMessage.ShowMessage -> LiveChatMessageUiModel.ShowMessage(
            author = message.author,
            message = message.message,
        )
        is FilteredLiveChatMessage.HiddenMessage -> LiveChatMessageUiModel.HiddenMessage(
            author = message.author,
            message = message.message,
            reason = reason,
        )
    }
}