package com.example.mystream.ui.core.uimodel

import com.example.mystream.domain.content.StreamingContentId
import kotlin.jvm.Throws

@Throws(IllegalArgumentException::class)
fun StreamingContentIdUiModel.toDomain() = StreamingContentId(id)

fun StreamingContentId.toUiModel() = StreamingContentIdUiModel(id)