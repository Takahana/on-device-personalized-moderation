package com.example.com.chat

import com.example.com.chat.entity.ChatRoomId
import com.example.mystream.shared.chat.LiveChatMessageBody
import com.example.mystream.shared.chat.LiveChatServerMessageBody
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.sendSerialized
import io.ktor.util.collections.ConcurrentMap

class ChatRoomService {
  private val sessions = ConcurrentMap<ChatRoomId, MutableSet<WebSocketServerSession>>()

  suspend fun join(
    roomId: ChatRoomId,
    session: WebSocketServerSession,
  ) {
    val room = sessions.getOrPut(roomId) { mutableSetOf() }
    room.add(session)
    session.sendSerialized(
      LiveChatServerMessageBody(
        sessionState = LiveChatServerMessageBody.SessionState.JOINED
      )
    )
  }

  fun leave(
    roomId: ChatRoomId,
    session: WebSocketServerSession,
  ) {
    val sessions = this.sessions[roomId]
    sessions?.remove(session)
    if (sessions != null && sessions.isEmpty()) {
      this.sessions.remove(roomId)
    }
  }

  suspend fun broadcast(
    roomId: ChatRoomId,
    newChatMessage: LiveChatMessageBody,
  ) {
    val sessions = this.sessions[roomId]
    if (sessions != null) {
      for (session in sessions) {
        session.sendSerialized(LiveChatServerMessageBody(
          sessionState = LiveChatServerMessageBody.SessionState.JOINED,
          newChatMessage = newChatMessage,
        ))
      }
    }
  }
}