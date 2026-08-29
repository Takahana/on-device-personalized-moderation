package com.example.mystream

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.mystream.ui.core.uimodel.StreamingContentIdUiModel
import com.example.mystream.ui.main.MainScreen
import com.example.mystream.ui.streamingcontent.StreamingContentWatchPageScreen
import com.example.mystream.ui.streamingcontent.StreamingContentWatchPageViewModel

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(
            onItemClick = { navKey -> backStack.add(navKey) },
            onToTestWatchPageButtonClick = {
              backStack.add(
                StreamingContentWatchPage(
                  streamingContentId = StreamingContentIdUiModel("test")
                )
              )
            },
            modifier = Modifier.safeDrawingPadding().padding(16.dp)
          )
        }
        entry<StreamingContentWatchPage> { navKey ->
          val viewModel = hiltViewModel<StreamingContentWatchPageViewModel, StreamingContentWatchPageViewModel.Factory>(
            creationCallback = { factory ->
              factory.create(navKey.streamingContentId)
            }
          )
          StreamingContentWatchPageScreen(
            viewModel = viewModel,
            modifier = Modifier.safeDrawingPadding().padding(16.dp),
          )
        }
      },
  )
}
