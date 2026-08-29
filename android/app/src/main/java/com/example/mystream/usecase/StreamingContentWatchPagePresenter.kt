package com.example.mystream.usecase

import com.example.mystream.domain.chat.LiveChatMessage

interface StreamingContentWatchPagePresenter {
  fun newMessage(message: LiveChatMessage)
}