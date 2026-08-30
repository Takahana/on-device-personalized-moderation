package com.example.mystream.service

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface LiveChatFilterModule {

  @Binds
  fun bindLiveChatFilter(defaultLiveChatFilter: DefaultLiveChatFilter): LiveChatFilter
}