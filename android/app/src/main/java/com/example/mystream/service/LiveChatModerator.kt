package com.example.mystream.service

import com.example.mystream.domain.chat.LiveChatMessage

interface LiveChatModerator {
  fun moderate(message: LiveChatMessage): LiveChatModerateResult
}