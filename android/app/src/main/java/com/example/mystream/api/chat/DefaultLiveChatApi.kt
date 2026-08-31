package com.example.mystream.api.chat

import com.example.mystream.BuildConfig
import com.example.mystream.logger.Logger
import com.example.mystream.shared.chat.LiveChatMessageBody
import com.example.mystream.shared.chat.LiveChatServerMessageBody
import com.example.mystream.shared.chat.LiveChatServerMessageBody.SessionState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class DefaultLiveChatApi(
  private val httpClient: HttpClient,
) : LiveChatApi {

  private val logger = Logger("DefaultLiveChatApi")
  private val baseUrl = BuildConfig.LIVE_CHAT_URL

  private var webSocketSession: WebSocketSession? = null
  private var sessionState: SessionState = SessionState.INITIAL

  override suspend fun connect(
    roomId: String,
    onJoined: () -> Unit,
    onMessageReceived: (LiveChatMessageBody) -> Unit,
  ) {
    webSocketSession?.close()
    val newSession = httpClient.webSocketSession {
      url("$baseUrl/ws/$roomId")
    }
    webSocketSession = newSession
    while (currentCoroutineContext().isActive) {
      val frame = newSession.incoming.receive() as? Frame.Text ?: continue
      val message = frame.readText()
      try {
        val serverMessage = Json.decodeFromString(LiveChatServerMessageBody.serializer(), message)
        when (serverMessage.sessionState) {
          SessionState.INITIAL -> {
            sessionState = SessionState.INITIAL
          }
          SessionState.JOINED -> {
            if (sessionState < SessionState.JOINED) {
              sessionState = SessionState.JOINED
              onJoined()
            }
          }
        }
        serverMessage.newChatMessage?.let { newMessage ->
          onMessageReceived(newMessage)
        }
      } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        logger.e("Failed to decode server message: $message", e)
      }
    }
    webSocketSession?.close()
    webSocketSession = null
  }

  override suspend fun sendMessage(
    roomId: String,
    message: LiveChatMessageBody
  ) {
    val session = webSocketSession ?: throw IllegalStateException("WebSocket session is not connected")
    val messageJson = Json.encodeToString(LiveChatMessageBody.serializer(), message)
    session.send(Frame.Text(messageJson))
    logger.d("Sent message: $messageJson")
  }
}