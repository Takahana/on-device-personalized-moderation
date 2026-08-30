package com.example.mystream.shared.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveChatServerMessageBody(
  @SerialName("session_state")
  val sessionState: SessionState,
  @SerialName("new_chat_message")
  val newChatMessage: LiveChatMessageBody? = null,
) {
  @Serializable
  enum class SessionState {
    INITIAL,
    JOINED,
  }
}
