package com.example.mystream.service

import com.example.mystream.data.GenAIRegexPatternGenerator
import com.example.mystream.data.RegexPatternRepository
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.service.FilteredLiveChatMessage.HiddenMessage.Reason
import javax.inject.Inject

class DefaultLiveChatFilter(
  private val moderator: LiveChatModerator,
) : LiveChatFilter {

  @Inject constructor(
    regexPatternRepository: RegexPatternRepository
  ) : this(
    CompositeLiveChatModerator(
      defaultModerator = DefaultLiveChatModerator(),
      dynamicModerator = DynamicLiveChatModerator(
        regexPatternRepository = regexPatternRepository,
      )
    )
  )

  override fun check(message: LiveChatMessage): FilteredLiveChatMessage {
    return when (moderator.moderate(message)) {
      LiveChatModerateResult.Hide ->
        FilteredLiveChatMessage.HiddenMessage(
          message = message,
          reason = Reason.BLOCKED_WORD,
        )

      LiveChatModerateResult.HideByAI ->
        FilteredLiveChatMessage.HiddenMessage(
          message = message,
          reason = Reason.BLOCKED_BY_AI,
        )

      LiveChatModerateResult.Show ->
        FilteredLiveChatMessage.ShowMessage(message)
    }
  }
}