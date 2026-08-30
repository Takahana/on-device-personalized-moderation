package com.example.mystream.service

import com.example.mystream.domain.chat.LiveChatMessage

class CompositeLiveChatModerator(
  private val defaultModerator: DefaultLiveChatModerator,
) : LiveChatModerator {

  override fun moderate(message: LiveChatMessage): LiveChatModerateResult {
    return defaultModerator.moderate(message)
  }
}