package com.example.com

import com.example.com.chat.ChatRoomService
import com.example.com.chat.entity.ChatRoomId
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import com.example.mystream.shared.chat.LiveChatMessageBody
import com.example.mystream.shared.chat.LiveChatServerMessageBody
import kotlinx.coroutines.launch

val chatRoomService = ChatRoomService()

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        webSocket("/ws") { // websocketSession
            chatRoomService.join(ChatRoomId("test"), this)
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        try {
                            val receivedMessage = Json.decodeFromString(LiveChatMessageBody.serializer(), frame.readText())
                            chatRoomService.broadcast(ChatRoomId("test"), receivedMessage)
                        } catch (_: Exception) {
                            continue
                        }
                    }
                }
            } finally {
                chatRoomService.leave(ChatRoomId("test"), this)
            }
        }
    }
}