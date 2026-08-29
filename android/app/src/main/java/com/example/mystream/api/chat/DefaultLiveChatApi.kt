package com.example.mystream.api.chat

import com.example.mystream.BuildConfig
import com.example.mystream.logger.Logger
import com.example.mystream.shared.chat.LiveChatMessageBody
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

  override suspend fun connect(
    roomId: String,
    onSessionStart: () -> Unit,
    onMessageReceived: (LiveChatMessageBody) -> Unit,
  ) {
    webSocketSession?.close()
    val newSession = httpClient.webSocketSession {
      url("$baseUrl/ws")
    }
    webSocketSession = newSession
    onSessionStart()
    while (currentCoroutineContext().isActive) {
      val frame = newSession.incoming.receive() as? Frame.Text ?: continue
      val message = frame.readText()
      val response = try {
        Json.decodeFromString(LiveChatMessageBody.serializer(), message)
      } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        logger.d("Failed to decode message: $message", e)
        null
      }
      if (response != null) {
        onMessageReceived(response)
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
  }
}