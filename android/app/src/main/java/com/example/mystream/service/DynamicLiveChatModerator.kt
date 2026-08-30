package com.example.mystream.service

import com.example.mystream.data.RegexPatternDataSource
import com.example.mystream.domain.chat.LiveChatMessage

class DynamicLiveChatModerator(
  private val regexPatternDataSource: RegexPatternDataSource,
) : LiveChatModerator {

  override fun moderate(message: LiveChatMessage): LiveChatModerateResult {
    regexPatternDataSource.getRegexPatterns().forEach { pattern ->
      if (message.message.matches(Regex(pattern))) {
        return LiveChatModerateResult.HideByAI
      }
    }
    return LiveChatModerateResult.Show
  }
}