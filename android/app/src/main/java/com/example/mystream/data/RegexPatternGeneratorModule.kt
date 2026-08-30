package com.example.mystream.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RegexPatternGeneratorModule {

  @Singleton
  @Provides
  fun provideRegexPatternGenerator(): RegexPatternGenerator {
    return GenAIRegexPatternGenerator()
  }
}