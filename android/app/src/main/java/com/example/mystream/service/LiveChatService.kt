package com.example.mystream.service

import com.example.mystream.api.chat.LiveChatApi
import com.example.mystream.domain.chat.ChatRoomId
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.shared.chat.LiveChatMessageBody
import javax.inject.Inject

class LiveChatService @Inject constructor(
  private val liveChatApi: LiveChatApi,
) {

  suspend fun connect(
    roomId: ChatRoomId,
    onSessionStart: () -> Unit,
    onMessageReceived: (LiveChatMessage) -> Unit
  ) {
    liveChatApi.connect(
      roomId.id,
      onSessionStart = onSessionStart,
      onMessageReceived = { response: LiveChatMessageBody ->
        val message = LiveChatMessage(
          author = response.author,
          message = response.message,
        )
        onMessageReceived(message)
      }
    )
  }

  suspend fun sendMessage(
    roomId: ChatRoomId,
    message: LiveChatMessage,
  ) {
    liveChatApi.sendMessage(
      roomId.id,
      LiveChatMessageBody(
        author = message.author,
        message = message.message,
      )
    )
  }
}