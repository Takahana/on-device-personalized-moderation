package com.example.mystream.api.chat

import com.example.mystream.shared.chat.LiveChatMessageBody

interface LiveChatApi {
  suspend fun connect(
    roomId: String,
    onJoined: () -> Unit,
    onMessageReceived: (LiveChatMessageBody) -> Unit,
  )

  @Throws(IllegalStateException::class)
  suspend fun sendMessage(roomId: String, message: LiveChatMessageBody)
}