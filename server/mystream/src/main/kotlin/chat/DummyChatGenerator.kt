package com.example.com.chat

import com.example.com.chat.entity.ChatRoomId
import com.example.mystream.shared.chat.LiveChatMessageBody
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DummyChatGenerator(
  private val coroutineScope: CoroutineScope,
  private val chatRoomService: ChatRoomService,
) {
  private val userCount = ConcurrentMap<ChatRoomId, Int>()
  private val jobs = ConcurrentMap<ChatRoomId, Job>()

  /**
   * はじめて1人以上のユーザーが参加したときに、ダミーのチャットメッセージを生成するジョブを開始する。
   * 5秒ごとにダミーのメッセージを生成し、5分後に自動的に停止する。
   */
  fun join(roomId: ChatRoomId) {
    userCount.compute(roomId) { _, count -> (count ?: 0) + 1 }
    if (jobs.containsKey(roomId)) return // Already running for this room

    val job = coroutineScope.launch {
      withTimeoutOrNull(5.minutes) { // Stop after 5 minutes
        var counter = 0
        while (true) {
          delay(5.seconds) // Generate a message every 5 seconds
          val message = LiveChatMessageBody(
            author = "DummyUser",
            message = DUMMY_CHAT_MESSAGES[counter++ % DUMMY_CHAT_MESSAGES.size],
          )
          chatRoomService.broadcast(roomId, message)
        }
      }
    }
    jobs[roomId] = job
  }

  fun leave(roomId: ChatRoomId) {
    userCount.compute(roomId) { _, count ->
      val newCount = (count ?: 0) - 1
      if (newCount <= 0) {
        jobs[roomId]?.cancel()
        jobs.remove(roomId)
        null // Remove the entry from userCount
      } else {
        newCount
      }
    }
  }

  private val DUMMY_CHAT_MESSAGES = listOf(
    "キックオフ！",
    "始まった！",
    "いい入り方",
    "前から行けよ",
    "今のパス雑すぎ",
    "トラップうま",
    "足遅くない？",
    "ビルドアップ怪しいな",
    "サイド変えるの遅い",
    "そこ決めろよ",
    "ナイスタックル",
    "戻り遅すぎ",
    "押し込め押し込め",
    "そのオーバーラップはいい",
    "クロス精度ひどい",
    "それ外すのかよ",
    "ナイスセーブ！",
    "キーパーしか仕事してない",
    "今のカットは良かった",
    "立ち位置微妙じゃない？",
    "もっと走れ",
    "前向けよ",
    "テンポ遅いな",
    "危なすぎる",
    "今のパスだけはうまい",
    "抜けた！",
    "打てよ！",
    "また外した",
    "決定力なさすぎ",
    "それ決められないのきつい",
    "守備だけは安定してる",
    "ナイスブロック",
    "見てて怖いわ",
    "カウンター遅い",
    "切り替え遅すぎ",
    "今の崩しはきれい",
    "やっと連携した",
    "ワンツーだけはうまい",
    "動き出し遅くない？",
    "そこ見えてるの偉い",
    "視野は広いな",
    "無理に狙うなよ",
    "ボール収まらなさすぎ",
    "キープできてない",
    "よく耐えた",
    "クロス適当すぎ",
    "ヘディング弱い",
    "また枠外",
    "何本外すんだよ",
    "これは決めないとダメ",
    "そろそろ点取れ",
    "流れ来てる",
    "強度は高い",
    "このまま押せ",
    "中盤負けてるぞ",
    "プレス甘い",
    "奪い返すの遅い",
    "運動量足りない",
    "今のはファウルだろ",
    "審判それ流すの？",
    "判定ひどくない？",
    "いい位置のFK",
    "どうせ壁だろ",
    "直接狙え",
    "そのボールはないわ",
    "またクリアされた",
    "コーナー！",
    "CK全然期待できない",
    "よく跳ね返した",
    "セカンド拾えよ",
    "シュート弱すぎ",
    "またブロック",
    "まだある！",
    "ゴール！！！",
    "やっと決めたか",
    "きたああああ！",
    "これはうまい",
    "今のだけは文句ない",
    "崩し完璧",
    "遅すぎるくらいだけどな",
    "スタジアム沸いてる",
    "ここで失点したら笑う",
    "集中切らすなよ",
    "守備怪しいぞ",
    "再開後ゆるすぎ",
    "また押し込まれてる",
    "ブロック低すぎない？",
    "ナイスカット",
    "そのファウルはいらない",
    "時間使えよ",
    "落ち着けって",
    "ボール持てないな",
    "パスミス多すぎ",
    "急に自信出てきたな",
    "またいい形",
    "ここで決めきれ",
    "危なすぎる",
    "またキーパー頼み",
    "キーパー神",
    "残り少ないぞ",
    "耐えろ",
    "この内容で勝てたらでかい",
    "正直試合内容は微妙",
    "勝ったけど課題多すぎ"
  )
}