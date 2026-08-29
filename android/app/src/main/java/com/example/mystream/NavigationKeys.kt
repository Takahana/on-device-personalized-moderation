package com.example.mystream

import androidx.navigation3.runtime.NavKey
import com.example.mystream.ui.uimodel.id.StreamingContentIdUiModel
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey

@Serializable data class StreamingContentWatchPage(
    val streamingContentId: StreamingContentIdUiModel,
) : NavKey