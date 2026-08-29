package com.example.com

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import com.example.mystream.shared.chat.LiveChatMessageBody

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        webSocket("/ws") { // websocketSession
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    try {
                        val receivedMessage = receiveDeserialized<LiveChatMessageBody>()
                        val newMessage = LiveChatMessageBody(author = "Server", message = "Hello, ${receivedMessage.author}!")
                        outgoing.send(Frame.Text(Json.encodeToString(LiveChatMessageBody.serializer(), newMessage)))
                    } catch (_: Exception) {
                        outgoing.send(Frame.Text("Invalid message format"))
                        continue
                    }
                }
            }
        }
    }
}