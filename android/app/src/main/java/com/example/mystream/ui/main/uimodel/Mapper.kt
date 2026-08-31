package com.example.mystream.ui.main.uimodel

import com.example.mystream.domain.content.StreamingContentCard
import com.example.mystream.ui.core.uimodel.toUiModel

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun List<StreamingContentCard>.mapToUiModel(): ImmutableList<StreamingContentCardUiModel> {
  return map { it.toUiModel() }.toImmutableList()
}

fun StreamingContentCard.toUiModel(): StreamingContentCardUiModel {
  return StreamingContentCardUiModel(
    id = id.toUiModel(),
    title = title,
  )
}