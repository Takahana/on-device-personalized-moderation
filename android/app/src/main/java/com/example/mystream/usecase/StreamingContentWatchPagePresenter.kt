package com.example.mystream.usecase

import com.example.mystream.service.FilteredLiveChatMessage

interface StreamingContentWatchPagePresenter {
  fun newMessage(message: FilteredLiveChatMessage)
  fun clearMessageInput()
  fun showError(errorType: PresentErrorType)
  fun updateRegexPatterns(patterns: Set<String>)
}

sealed interface PresentErrorType {
  object SendMessageFailed : PresentErrorType
  object PersonalizationUnsupported : PresentErrorType
}