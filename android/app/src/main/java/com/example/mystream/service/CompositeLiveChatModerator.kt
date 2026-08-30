package com.example.mystream.service

import com.example.mystream.domain.chat.LiveChatMessage

class CompositeLiveChatModerator(
  private val defaultModerator: DefaultLiveChatModerator,
  private val dynamicModerator: DynamicLiveChatModerator
) : LiveChatModerator {

  override fun moderate(message: LiveChatMessage): LiveChatModerateResult {
    defaultModerator.moderate(message).let { result ->
      if (result < LiveChatModerateResult.Show) {
        return result
      }
    }
    dynamicModerator.moderate(message).let { result ->
      if (result < LiveChatModerateResult.Show) {
        return result
      }
    }
    return LiveChatModerateResult.Show
  }
}