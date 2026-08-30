package com.example.mystream.ui.streamingcontent.uimodel

import com.example.mystream.domain.chat.LiveChatMessage

fun List<LiveChatMessage>.mapToUiModel(): List<LiveChatMessageUiModel> {
  return map { it.toUiModel() }
}

fun LiveChatMessage.toUiModel(): LiveChatMessageUiModel {
    return LiveChatMessageUiModel(
        author = this.author,
        message = this.message,
    )
}