package com.example.mystream.usecase

import com.example.mystream.data.RegexPatternRepository
import com.example.mystream.domain.chat.ChatRoomId
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.domain.content.StreamingContentId
import com.example.mystream.logger.Logger
import com.example.mystream.service.LiveChatService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mystream.usecase.StreamingContentWatchPageUseCase.StreamingContentWatchPageEvent.Joined
import kotlinx.coroutines.delay

/**
 * 配信中コンテンツの視聴ページのユースケース。
 */
class StreamingContentWatchPageUseCase(
  private val presenter: StreamingContentWatchPagePresenter,
  private val liveChatService: LiveChatService,
  private val regexPatternRepository: RegexPatternRepository,
) {
  private val eventHandler = Channel<StreamingContentWatchPageEvent>(Channel.UNLIMITED)

  private val logger = Logger("StreamingContentWatchPageUseCase")

  /**
   * 初回表示
   */
  suspend fun display(
    streamingContentId: StreamingContentId,
  ) {
    val chatRoomId = ChatRoomId(streamingContentId.id)
    coroutineScope {
      launch {
        for (event in eventHandler) {
          when (event) {
            is Joined -> onJoined()
          }
        }
      }
      launch {
        liveChatService.connect(
          chatRoomId,
          onJoined = {
            eventHandler.trySend(Joined)
          },
          onMessageReceived = { message ->
            presenter.newMessage(message)
          }
        )
      }
      launch {
        regexPatternRepository.observeRegexPatterns().collect { patterns ->
          presenter.updateRegexPatterns(patterns)
        }
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
      presenter.clearMessageInput()
    } catch (e: Exception) {
      logger.e("Failed to send message: ${e.message}", e)
      presenter.showError(PresentErrorType.SendMessageFailed)
    }
  }

  private suspend fun onJoined() {
    val newMessage = LiveChatMessage(
      author = "User",
      message = "Hi!"
    )
    liveChatService.sendMessage(
      ChatRoomId("test"),
      message = newMessage,
    )
  }

  class Factory @Inject constructor(
    private val liveChatService: LiveChatService,
    private val regexPatternRepository: RegexPatternRepository,
  ) {
    fun create(
      presenter: StreamingContentWatchPagePresenter,
    ): StreamingContentWatchPageUseCase {
      return StreamingContentWatchPageUseCase(presenter, liveChatService, regexPatternRepository)
    }
  }

  private sealed class StreamingContentWatchPageEvent {
    object Joined : StreamingContentWatchPageEvent()
  }
}