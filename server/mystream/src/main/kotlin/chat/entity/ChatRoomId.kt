package com.example.com.chat.entity

data class ChatRoomId(
    val id: String
) {
  init {
    require(id.isNotBlank()) { "ChatRoomId cannot be blank" }
  }
}