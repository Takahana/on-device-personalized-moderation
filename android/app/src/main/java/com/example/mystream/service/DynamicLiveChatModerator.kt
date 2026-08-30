package com.example.mystream.service

import com.example.mystream.data.RegexPatternRepository
import com.example.mystream.domain.chat.LiveChatMessage

class DynamicLiveChatModerator(
  private val regexPatternRepository: RegexPatternRepository,
) : LiveChatModerator {

  override fun moderate(message: LiveChatMessage): LiveChatModerateResult {
    regexPatternRepository.getRegexPatterns().forEach { pattern ->
      if (Regex(pattern).containsMatchIn(message.message)) {
        return LiveChatModerateResult.HideByAI
      }
    }
    return LiveChatModerateResult.Show
  }
}