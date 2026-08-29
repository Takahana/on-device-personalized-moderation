package com.example.mystream.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import jakarta.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class HttpClientModule {

  @Singleton
  @Provides
  fun provideHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
      install(WebSockets)
    }
  }
}