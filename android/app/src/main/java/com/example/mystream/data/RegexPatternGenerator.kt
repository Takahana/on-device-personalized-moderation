package com.example.mystream.data

interface RegexPatternGenerator {
  suspend fun generateRegexPatterns(
    userPreference: String,
    context: String,
  ): Result

  data class Result(
    val newPatterns: Set<String>,
    val removedPatterns: Set<String>
  )
}