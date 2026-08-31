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
      ユーザーの好みと現在の番組コンテキストから、これからライブチャットに出現しそうな、ユーザーが見たくないコメントを先回りして予測し、それらを広く検出できる正規表現を最大5件生成してください。
      目的は、すでに受信したコメントを分類することではありません。
      まだ受信していない今後のコメントを予測してフィルターを準備することです。
      チャット履歴は、現在の番組の雰囲気、話題、視聴者が使う語彙を推測するための参考情報としてのみ使用してください。
      履歴に存在するコメントの抽出や言い換えだけに限定しないでください。
      生成するパターンには、以下をバランスよく含めてください。
      対象語がなくても成立する、否定的・辛辣な評価表現
      現在の番組で今後使われそうな、人物・行動・態度などへの否定的評価表現
      同じ意味を持つ複数の言い回しをまとめたパターン
      例:
      (重すぎ|必死すぎ|めんどくさ)
      (あざと|計算して|わざとらし)
      (態度|性格).*(悪|きつ|面倒)
      重要:
      正規表現は containsMatchIn による部分一致で使用します。
      入力されたコメントそのものをコピーするだけのパターンは避けてください。
      履歴にまだ出ていなくても、コンテキストから今後出現しそうなら生成してください。
      具体的な名詞を必ず含める必要はありません。
      短いコメントにもマッチできるよう、対象語を要求しすぎないでください。
      各パターンは、それ単体でユーザーが見たくない可能性が高い表現にマッチしてください。
      ポジティブ・中立な表現にはマッチさせないでください。
      元のユーザーの好みよりも過激・有害な表現へ拡張しないでください。
      .* と (A|B|C) を中心に、短くシンプルな正規表現にしてください。
      最大5件のうち、少なくとも2件は対象語を必要としない汎用的なパターンにしてください。

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