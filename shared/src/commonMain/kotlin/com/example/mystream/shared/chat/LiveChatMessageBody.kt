package com.example.mystream.shared.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveChatMessageBody(
  @SerialName("author")
  val author: String,
  @SerialName("message")
  val message: String,
)