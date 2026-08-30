package com.example.mystream.service

import com.example.mystream.domain.chat.LiveChatMessage

interface LiveChatFilter {
  fun check(message: LiveChatMessage): FilteredLiveChatMessage
}