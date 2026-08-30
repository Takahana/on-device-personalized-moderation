package com.example.mystream.data

import com.example.mystream.logger.Logger
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerateContentRequest
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.google.mlkit.genai.prompt.ModelPreference
import com.google.mlkit.genai.prompt.ModelReleaseStage
import com.google.mlkit.genai.prompt.SystemInstruction
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import com.google.mlkit.genai.prompt.generateTypedContentRequest
import com.google.mlkit.genai.prompt.generationConfig
import com.google.mlkit.genai.prompt.modelConfig
import com.google.mlkit.genai.schema.annotations.Generable
import com.google.mlkit.genai.schema.annotations.Guide

class GenAIRegexPatternGenerator : RegexPatternGenerator {

  private val logger = Logger("GenAIRegexPatternGenerator")

  private val config by lazy {
    generationConfig {
      modelConfig = modelConfig {
        releaseStage = ModelReleaseStage.STABLE
      }
    }
  }

  private val generativeModel by lazy {
    Generation.getClient(config)
  }

  private val systemInstruction = SystemInstruction("""
    あなたはライブチャットのパーソナライズフィルターです。

    サーバー側のモデレーションを通過したコメントを対象に、ユーザーが不快に感じる可能性のある軽度〜中程度のネガティブ表現、辛辣な批判、煽りなどを検出する正規表現を生成してください。

    入力コメントをそのままコピーせず、意味の近い表現にもマッチするように一般化してください。
    ただし、元の傾向から大きく逸脱した、より過激・攻撃的・有害な表現には拡張しないでください。

    正規表現は短くシンプルにし、説明文は出力しないでください。
  """.trimIndent())

  override suspend fun generateRegexPatterns(
    userPreference: String,
    context: String,
  ): RegexPatternGenerator.Result {
    when (generativeModel.checkStatus()) {
      FeatureStatus.UNAVAILABLE -> {
        logger.e("Generative model is unavailable")
        throw UnsupportedOperationException("Generative model is unavailable")
      }

      FeatureStatus.DOWNLOADABLE,
      FeatureStatus.DOWNLOADING -> {
        if (!awaitDownload(generativeModel)) {
          return RegexPatternGenerator.Result(
            newPatterns = emptySet(),
            removedPatterns = emptySet()
          )
        }
      }

      FeatureStatus.AVAILABLE -> {
        logger.d("Generative model is available")
      }
    }
    if(!generativeModel.isStructuredOutputFeatureAvailable()) {
      logger.e("Structured output feature is unavailable")
      throw UnsupportedOperationException("Structured output feature is unavailable")
    }

    val prompt = """
      ユーザーの好みと現在のコンテキストをもとに、今後ライブチャットで出現しそうな、ユーザーが表示したくない可能性のある表現を予測し、最大5件の正規表現を生成してください。

      チャット履歴は現在の雰囲気や語彙を知るための参考情報として使用し、履歴の言い換えだけに限定しないでください。

      生成するパターンには、以下の両方を含めてください。

      * コンテキストに依存せず、さまざまなコメントで使われる辛辣・否定的な表現
      * 現在のコンテキストで使われそうな対象語と否定的評価を組み合わせた表現

      例:

      汎用的なパターン:
      (雑すぎ|ひどい|微妙すぎ)
      (遅すぎ|足りない|できてない)

      コンテキストを考慮したパターン:
      (説明|内容).*(雑|分かりにく)
      (対応|反応).*(遅|鈍)

      重要:

      * 入力チャットそのもののコピーだけにしないでください。
      * 正規表現は部分一致で利用します。
      * 具体的な名詞を必ず含める必要はありません。
      * 各パターンは、それ単体でユーザーが表示したくない表現だけにマッチするようにしてください。
      * ポジティブまたは中立な表現を選択肢に含めないでください。
      * 無関係な意味カテゴリや、より過激・有害な表現には拡張しないでください。
      * 短くシンプルな正規表現を生成してください。

      ユーザーの好み:
      $userPreference

      コンテキスト（参考となるチャット履歴）:
      $context
    """.trimIndent()
    logger.d("prompt: $prompt")
    val baseRequest = GenerateContentRequest.Builder(TextPart(prompt))
      .apply {
        this@apply.systemInstruction = this@GenAIRegexPatternGenerator.systemInstruction
        this.maxOutputTokens = 120
      }
      .build()
    val typedRequest = generateTypedContentRequest(
      generateContentRequest = baseRequest,
      outputClass = RegexPatternList::class,
      includeSchemaInPrompt = true
    )
    val now = System.currentTimeMillis()
    logger.d("Sending request to generative model")
    val response = generativeModel.generateContent(typedRequest)
    logger.d("Received response in ${System.currentTimeMillis() - now} ms")
    val generatedList = response.candidates.firstOrNull()?.response
    if (generatedList == null || generatedList.patterns.isEmpty()) {
      logger.e("No generated list received")
      return RegexPatternGenerator.Result(
        newPatterns = emptySet(),
        removedPatterns = emptySet()
      )
    }
    val newPatterns = generatedList.patterns.filter {
      runCatching { Regex(it) }.getOrNull() != null
    }.toSet()
    logger.d("Generated new patterns: $newPatterns")
    return RegexPatternGenerator.Result(
      newPatterns = newPatterns,
      removedPatterns = emptySet()
    )
  }

  private suspend fun awaitDownload(generativeModel: GenerativeModel): Boolean {
    var success = false
    generativeModel.download().collect { status ->
      when (status) {
        is DownloadStatus.DownloadStarted -> {
          logger.d("Download started")
        }
        is DownloadStatus.DownloadProgress -> {
          logger.d("Downloaded ${status.totalBytesDownloaded} bytes")
        }
        is DownloadStatus.DownloadCompleted -> {
          logger.d("Download completed")
          success = true
        }
        is DownloadStatus.DownloadFailed -> {
          logger.e("Download failed: ${status.e.message}")
          success = false
        }
      }
    }
    return success
  }
}

@Generable
data class RegexPatternList(
  @param:Guide(
    description = "List of regex patterns generated for live chat moderation",
    minItems = 1,
    maxItems = 5,
  )
  val patterns: List<String>,
)