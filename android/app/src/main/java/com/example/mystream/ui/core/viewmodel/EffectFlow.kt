package com.example.mystream.ui.core.viewmodel

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun <T> mutableEffectFlow() = MutableSharedFlow<T>(
  replay = 0,
  extraBufferCapacity = 5,
  onBufferOverflow = BufferOverflow.DROP_OLDEST,
)