package com.example.mystream.usecase

import com.example.mystream.domain.chat.ChatRoomId
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.domain.content.StreamingContentId
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

  sealed class StreamingContentWatchPageEvent {
    object SessionStarted : StreamingContentWatchPageEvent()
  }
}