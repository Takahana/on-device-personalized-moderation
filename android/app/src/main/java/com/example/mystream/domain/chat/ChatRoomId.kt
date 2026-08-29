package com.example.mystream.domain.chat

data class ChatRoomId(
  val id: String,
) {
  init {
    require(id.isNotBlank()) { "ChatRoomId cannot be blank" }
  }
}