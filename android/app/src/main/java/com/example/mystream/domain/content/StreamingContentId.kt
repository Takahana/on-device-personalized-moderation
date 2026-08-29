package com.example.mystream.domain.content

data class StreamingContentId(val id: String) {
    init {
        require(id.isNotBlank()) { "StreamingContentId cannot be blank" }
    }
}