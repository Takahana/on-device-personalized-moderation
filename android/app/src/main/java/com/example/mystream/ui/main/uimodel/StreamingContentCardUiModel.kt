package com.example.mystream.ui.main.uimodel

import androidx.compose.runtime.Immutable
import com.example.mystream.ui.core.uimodel.StreamingContentIdUiModel

@Immutable
data class StreamingContentCardUiModel(
  val id: StreamingContentIdUiModel,
  val title: String,
)