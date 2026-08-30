package com.example.com

import com.example.com.chat.ChatRoomService
import com.example.com.chat.DummyChatGenerator
import com.example.com.chat.entity.ChatRoomId
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import com.example.mystream.shared.chat.LiveChatMessageBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

fun Application.configureRouting() {
    val applicationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )
    monitor.subscribe(ApplicationStopped) {
        applicationScope.cancel()
    }
    val chatRoomService = ChatRoomService()
    val dummyChatGenerator = DummyChatGenerator(applicationScope, chatRoomService)

    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        webSocket("/ws") { // websocketSession
            val chatRoomId = ChatRoomId("test")
            chatRoomService.join(chatRoomId, this)
            dummyChatGenerator.join(chatRoomId)
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        try {
                            val receivedMessage = Json.decodeFromString(LiveChatMessageBody.serializer(), frame.readText())
                            chatRoomService.broadcast(chatRoomId, receivedMessage)
                        } catch (_: Exception) {
                            continue
                        }
                    }
                }
            } finally {
                chatRoomService.leave(chatRoomId, this)
                dummyChatGenerator.leave(chatRoomId)
            }
        }
    }
}