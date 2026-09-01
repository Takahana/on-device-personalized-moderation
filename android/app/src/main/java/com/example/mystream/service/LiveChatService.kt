package com.example.mystream.service

import com.example.mystream.api.chat.LiveChatApi
import com.example.mystream.data.RegexPatternRepository
import com.example.mystream.domain.chat.ChatRoomId
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.shared.chat.LiveChatMessageBody
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class LiveChatService @Inject constructor(
  private val liveChatApi: LiveChatApi,
  private val liveChatFilter: LiveChatFilter,
  private val regexPatternRepository: RegexPatternRepository,
) {

  // パーソナライズ用に50件まで保持しておく
  private val receivedMessages = mutableListOf<LiveChatMessage>()

  suspend fun connect(
    roomId: ChatRoomId,
    onJoined: () -> Unit,
    onMessageReceived: (FilteredLiveChatMessage) -> Unit,
    onPersonalizationUnsupported: () -> Unit,
  ) = coroutineScope {
    launch {
      liveChatApi.connect(
        roomId.id,
        onJoined = onJoined,
        onMessageReceived = { response: LiveChatMessageBody ->
          val message = LiveChatMessage(
            author = response.author,
            message = response.message,
          )
          onMessageReceived(liveChatFilter.check(message))
          receivedMessages.add(message)
          if (receivedMessages.size > 50) {
            receivedMessages.removeAt(0)
          }
        }
      )
    }
    launch {
      val content = when (roomId.id) {
        "soccer" -> "サッカー。勝負を分ける一瞬の判断。攻守が激しく入れ替わる、日本代表の熱戦を見逃すな。"
        "news" -> "政治ニュース。物価高対策の行方は。与野党がぶつかる国会論戦、その発言と対応に注目が集まります。"
        "variety" -> "お笑いバラエティ。芸人たちが体当たり企画に挑戦。予測不能の展開と爆笑の瞬間をお届けします。"
        "reality" -> "恋愛リアリティショー。揺れる気持ちとすれ違う想い。恋の矢印が大きく動く、運命の夜が始まります。"
        else -> "配信中コンテンツ"
      }
      val userPreference = when (roomId.id) {
        "soccer" -> "選手のミスやプレーを強く責めるコメントは見たくない。"
        "news" -> "政治家や政策を一方的に馬鹿にするようなコメントは見たくない。"
        "variety" -> "芸人やネタをつまらない、滑っていると強く否定するコメントは見たくない。"
        "reality" -> "出演者の性格や恋愛行動を決めつけて批判するコメントは見たくない。"
        else -> "攻撃的なコメントは見たくない"
      }
      while (true) {
        try {
          regexPatternRepository.personalize(
            userPreference = userPreference,
            context = """
              番組概要：$content
              以下は受信したコメントのカンマ区切りのリストです：
              [${receivedMessages.joinToString(separator = ",") { it.message }}]
            """.trimIndent()
          )
          currentCoroutineContext().ensureActive()
          delay(30.seconds)
        } catch (e: UnsupportedOperationException) {
          onPersonalizationUnsupported()
          break
        }
      }
    }
  }

  suspend fun sendMessage(
    roomId: ChatRoomId,
    message: LiveChatMessage,
  ) {
    liveChatApi.sendMessage(
      roomId.id,
      LiveChatMessageBody(
        author = message.author,
        message = message.message,
      )
    )
  }
}