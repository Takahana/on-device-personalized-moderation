package com.example.mystream.data

import com.google.mlkit.genai.common.GenAiException
import kotlinx.serialization.Serializable

interface RegexPatternGenerator {
  @Throws(GenAiException::class)
  suspend fun generateRegexPatterns(
    userPreference: String,
    context: String,
  ): GeneratedRegexPatternsResult
}

@Serializable
data class GeneratedRegexPatternsResult(
  val newPatterns: Set<String>,
  val removedPatterns: Set<String>
)