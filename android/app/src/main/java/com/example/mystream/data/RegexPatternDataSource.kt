package com.example.mystream.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class RegexPatternDataSource {

  private val cache = MutableStateFlow<Set<String>>(emptySet())

  fun getRegexPatterns(): Sequence<String> = cache.value.asSequence()

  fun addRegexPattern(pattern: String) {
    cache.update { currentSet ->
      currentSet + pattern
    }
  }

  fun removeRegexPattern(pattern: String) {
    cache.update { currentSet ->
      currentSet - pattern
    }
  }
}