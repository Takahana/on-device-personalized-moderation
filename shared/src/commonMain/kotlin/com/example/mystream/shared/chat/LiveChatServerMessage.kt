package com.example.mystream.shared.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveChatServerMessageBody(
  @SerialName("session_state")
  val sessionState: SessionState,
) {
  @Serializable
  enum class SessionState {
    JOINED,
  }
}
