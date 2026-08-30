package com.example.mystream.usecase

import com.example.mystream.domain.chat.LiveChatMessage

interface StreamingContentWatchPagePresenter {
  fun newMessage(message: LiveChatMessage)
  fun clearMessageInput()
  fun showError(errorType: PresentErrorType)
}

sealed interface PresentErrorType {
  object SendMessageFailed : PresentErrorType
}