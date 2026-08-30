package com.example.mystream.service

import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.service.FilteredLiveChatMessage.HiddenMessage.Reason
import javax.inject.Inject

class DefaultLiveChatFilter(
  private val blockedWords: List<String> = DEFAULT_BLOCKED_WORDS,
) : LiveChatFilter {

  @Inject constructor() : this(DEFAULT_BLOCKED_WORDS)

  override fun check(message: LiveChatMessage): FilteredLiveChatMessage {
    val isBlocked = blockedWords.any { blockedWord ->
      message.message.contains(blockedWord)
    }
    return if (isBlocked) {
      FilteredLiveChatMessage.HiddenMessage(
        message = message,
        reason = Reason.BLOCKED_WORD,
      )
    } else {
      FilteredLiveChatMessage.ShowMessage(message)
    }
  }

  companion object {
    val DEFAULT_BLOCKED_WORDS = listOf(
      "そこ決めろよ",
      "それ外すのかよ",
    )
  }
}