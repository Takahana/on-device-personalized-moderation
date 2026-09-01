package com.example.mystream.data

import com.google.mlkit.genai.common.GenAiException

interface RegexPatternGenerator {
  @Throws(GenAiException::class)
  suspend fun generateRegexPatterns(
    userPreference: String,
    context: String,
  ): Result

  data class Result(
    val newPatterns: Set<String>,
    val removedPatterns: Set<String>
  )
}