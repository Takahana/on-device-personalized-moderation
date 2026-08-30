package com.example.com.content.entity

data class StreamingContentId(
    val id: String
) {
  init {
    require(id.isNotBlank()) { "StreamingContentId cannot be blank" }
  }
}