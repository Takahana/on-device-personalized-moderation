package com.example.mystream.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@Singleton
class RegexPatternRepository @Inject constructor(
  private val regexPatternGenerator: RegexPatternGenerator,
) {

  private val cache = MutableStateFlow<Set<String>>(emptySet())

  fun getRegexPatterns(): Sequence<String> = cache.value.asSequence()

  fun addRegexPattern(pattern: String) {
    cache.update { currentSet ->
      currentSet + pattern
    }
  }

  fun addAllRegexPattern(patterns: Set<String>) {
    cache.update { currentSet ->
      currentSet + patterns
    }
  }

  fun removeRegexPattern(pattern: String) {
    cache.update { currentSet ->
      currentSet - pattern
    }
  }

  fun removeAllRegexPattern(patterns: Set<String>) {
    cache.update { currentSet ->
      currentSet - patterns
    }
  }

  fun observeRegexPatterns(): Flow<Set<String>> = cache.asStateFlow()

  @Throws(UnsupportedOperationException::class)
  suspend fun personalize(
    userPreference: String,
    context: String,
  ) {
    val result = regexPatternGenerator.generateRegexPatterns(
      userPreference = userPreference,
      context = context
    )
    addAllRegexPattern(result.newPatterns)
    removeAllRegexPattern(result.removedPatterns)
  }
}