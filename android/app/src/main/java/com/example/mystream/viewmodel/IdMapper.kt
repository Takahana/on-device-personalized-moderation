package com.example.mystream.viewmodel

import com.example.mystream.domain.content.StreamingContentId
import com.example.mystream.ui.uimodel.id.StreamingContentIdUiModel
import kotlin.jvm.Throws

@Throws(IllegalArgumentException::class)
fun StreamingContentIdUiModel.toDomain() = StreamingContentId(id)