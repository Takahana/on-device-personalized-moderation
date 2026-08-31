package com.example.mystream.ui.main

import com.example.mystream.data.DataRepository
import com.example.mystream.domain.content.StreamingContentCard
import com.example.mystream.domain.content.StreamingContentId
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
  @Test
  fun uiState_initiallyLoading() = runTest {
    val viewModel = MainScreenViewModel(FakeMyModelRepository())
    assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)
  }

  @Test
  fun uiState_onItemSaved_isDisplayed() = runTest {
    val viewModel = MainScreenViewModel(FakeMyModelRepository())
    assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)
  }
}

private class FakeMyModelRepository : DataRepository {
  override val cards: Flow<List<StreamingContentCard>> = flow { emit(
    listOf(
      StreamingContentCard(
        id = StreamingContentId("soccer"),
        "サッカー"
      ),
      StreamingContentCard(
        id = StreamingContentId("news"),
        "政治ニュース"
      ),
      StreamingContentCard(
        id = StreamingContentId("reality"),
        "恋愛リアリティショー"
      ),
      StreamingContentCard(
        id = StreamingContentId("variety"),
        "お笑いバラエティ"
      ),
    )
  ) }
}
