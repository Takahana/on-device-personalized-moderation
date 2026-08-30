package com.example.mystream.service

import com.example.mystream.api.chat.LiveChatApi
import com.example.mystream.domain.chat.ChatRoomId
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.shared.chat.LiveChatMessageBody
import javax.inject.Inject

class LiveChatService @Inject constructor(
  private val liveChatApi: LiveChatApi,
  private val liveChatFilter: LiveChatFilter,
) {

  suspend fun connect(
    roomId: ChatRoomId,
    onJoined: () -> Unit,
    onMessageReceived: (FilteredLiveChatMessage) -> Unit
  ) {
    liveChatApi.connect(
      roomId.id,
      onJoined = onJoined,
      onMessageReceived = { response: LiveChatMessageBody ->
        val message = LiveChatMessage(
          author = response.author,
          message = response.message,
        )
        onMessageReceived(liveChatFilter.check(message))
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