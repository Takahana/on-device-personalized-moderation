package com.example.mystream.ui.streamingcontent.uimodel

import androidx.compose.runtime.Immutable

@Immutable
data class LiveChatMessageUiModel(
    val author: String,
    val message: String,
)