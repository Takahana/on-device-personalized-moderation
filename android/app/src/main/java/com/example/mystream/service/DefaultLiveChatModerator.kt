package com.example.mystream.service

import com.example.mystream.domain.chat.LiveChatMessage

class DefaultLiveChatModerator(
  private val blockedWords: List<String> = DEFAULT_BLOCKED_WORDS,
) : LiveChatModerator {
  override fun moderate(message: LiveChatMessage): LiveChatModerateResult {
    val containsBlockedWord = blockedWords.any { message.message.contains(it) }
    return if (containsBlockedWord) {
      LiveChatModerateResult.Hide
    } else {
      LiveChatModerateResult.Show
    }
  }

  companion object {
    val DEFAULT_BLOCKED_WORDS = listOf(
      "そこ決めろよ",
      "それ外すのかよ",
    )
  }
}