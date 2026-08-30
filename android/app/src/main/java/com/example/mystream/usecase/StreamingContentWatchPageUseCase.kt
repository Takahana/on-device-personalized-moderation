package com.example.mystream.usecase

import com.example.mystream.domain.chat.ChatRoomId
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.domain.content.StreamingContentId
import com.example.mystream.logger.Logger
import com.example.mystream.service.LiveChatService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mystream.usecase.StreamingContentWatchPageUseCase.StreamingContentWatchPageEvent.SessionStarted

/**
 * 配信中コンテンツの視聴ページのユースケース。
 */
class StreamingContentWatchPageUseCase(
  private val presenter: StreamingContentWatchPagePresenter,
  private val liveChatService: LiveChatService,
) {
  private val eventHandler = Channel<StreamingContentWatchPageEvent>(Channel.UNLIMITED)

  private val logger = Logger("StreamingContentWatchPageUseCase")

  /**
   * 初回表示
   */
  suspend fun display(
    streamingContentId: StreamingContentId,
  ) {
    val chatRoomId = ChatRoomId("test")
    coroutineScope {
      launch {
        for (event in eventHandler) {
          when (event) {
            is SessionStarted -> onSessionStart()
          }
        }
      }
      launch {
        liveChatService.connect(
          chatRoomId,
          onSessionStart = {
            eventHandler.trySend(SessionStarted)
          },
          onMessageReceived = { message ->
            presenter.newMessage(message)
          }
        )
      }
    }
  }

  suspend fun sendMessage(message: String) {
    try {
      val newMessage = LiveChatMessage(
        author = "User",
        message = message
      )
      liveChatService.sendMessage(
        ChatRoomId("test"),
        message = newMessage,
      )
      presenter.newMessage(newMessage)
      presenter.clearMessageInput()
    } catch (e: Exception) {
      logger.e("Failed to send message: ${e.message}", e)
      presenter.showError(PresentErrorType.SendMessageFailed)
    }
  }

  private suspend fun onSessionStart() {
    val newMessage = LiveChatMessage(
      author = "User",
      message = "Hi!"
    )
    presenter.newMessage(newMessage)
    liveChatService.sendMessage(
      ChatRoomId("test"),
      message = newMessage,
    )
  }

  class Factory @Inject constructor(
    private val liveChatService: LiveChatService,
  ) {
    fun create(
      presenter: StreamingContentWatchPagePresenter,
    ): StreamingContentWatchPageUseCase {
      return StreamingContentWatchPageUseCase(presenter, liveChatService)
    }
  }

  private sealed class StreamingContentWatchPageEvent {
    object SessionStarted : StreamingContentWatchPageEvent()
  }
}