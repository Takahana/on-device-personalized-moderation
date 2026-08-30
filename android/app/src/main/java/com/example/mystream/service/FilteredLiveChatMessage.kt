package com.example.mystream.service

import com.example.mystream.domain.chat.LiveChatMessage

sealed interface FilteredLiveChatMessage {
  data class ShowMessage(
    val message: LiveChatMessage,
  ) : FilteredLiveChatMessage
  data class HiddenMessage(
    val message: LiveChatMessage,
    val reason: Reason,
  ) : FilteredLiveChatMessage {
    enum class Reason {
      BLOCKED_WORD,
      BLOCKED_BY_AI,
    }
  }
}