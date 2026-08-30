package com.example.mystream.usecase

import com.example.mystream.service.FilteredLiveChatMessage

interface StreamingContentWatchPagePresenter {
  fun newMessage(message: FilteredLiveChatMessage)
  fun clearMessageInput()
  fun showError(errorType: PresentErrorType)
}

sealed interface PresentErrorType {
  object SendMessageFailed : PresentErrorType
}