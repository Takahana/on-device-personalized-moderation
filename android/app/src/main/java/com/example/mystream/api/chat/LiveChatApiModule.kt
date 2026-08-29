package com.example.mystream.api.chat

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import jakarta.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LiveChatApiModule {

  @Singleton
  @Provides
  fun provideLiveChatApi(
    httpClient: HttpClient,
  ): LiveChatApi {
    return DefaultLiveChatApi(httpClient = httpClient)
  }
}