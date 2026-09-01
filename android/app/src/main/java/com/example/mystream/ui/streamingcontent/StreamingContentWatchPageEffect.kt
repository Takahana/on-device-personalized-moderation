package com.example.mystream.ui.streamingcontent

sealed interface StreamingContentWatchPageEffect {
  data class ShowErrorToast(val errorType: ErrorToastType) : StreamingContentWatchPageEffect
}

sealed interface ErrorToastType {
  object SendMessageFailed : ErrorToastType
  object PersonalizationUnsupported : ErrorToastType
}