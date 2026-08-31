package com.example.mystream.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.mystream.R
import com.example.mystream.data.DefaultDataRepository
import com.example.mystream.domain.content.StreamingContentCard
import com.example.mystream.domain.content.StreamingContentId
import com.example.mystream.theme.MyStreamTheme
import com.example.mystream.ui.core.uimodel.StreamingContentIdUiModel
import com.example.mystream.ui.main.uimodel.StreamingContentCardUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MainScreen(
  onContentCardClick: (StreamingContentIdUiModel) -> Unit ,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  when (state) {
    MainScreenUiState.Loading -> {
      // Blank
    }
    is MainScreenUiState.Success -> {
      MainScreen(
        cards = (state as MainScreenUiState.Success).cards,
        onContentCardClick = onContentCardClick,
        modifier = modifier
      )
    }
    is MainScreenUiState.Error -> {
      Text("Error loading data: ${(state as MainScreenUiState.Error).throwable.message}")
    }
  }
}

@Composable
internal fun MainScreen(
  cards: ImmutableList<StreamingContentCardUiModel>,
  onContentCardClick: (StreamingContentIdUiModel) -> Unit = {},
  modifier: Modifier = Modifier,
) {
  LazyVerticalGrid(
    modifier = modifier,
    columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(150.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(cards.size) { index ->
      StreamingContentCard(
        content = cards[index],
        onItemClick = onContentCardClick,
        modifier = Modifier
      )
    }
  }
}

@Composable
fun StreamingContentCard(
  content: StreamingContentCardUiModel,
  onItemClick: (StreamingContentIdUiModel) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    onClick = { onItemClick(content.id) }
  ) {
    Image(
      painter = painterResource(id = content.thumbnail()),
      contentDescription = content.title,
      modifier = Modifier.aspectRatio(16f / 9f)
    )
    Text(
      text = content.title,
      modifier = Modifier.padding(8.dp)
    )
  }
}

@DrawableRes
internal fun StreamingContentCardUiModel.thumbnail(): Int {
  return when (id.id) {
    "soccer" -> R.drawable.thumb_soccer
    "news" -> R.drawable.thumb_news
    "reality" -> R.drawable.thumb_reality
    "variety" -> R.drawable.thumb_variety
    else -> throw IllegalArgumentException("Unknown content id: ${id.id}")
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  MyStreamTheme { MainScreen(
    cards =
      persistentListOf(
        StreamingContentCardUiModel(
          id = StreamingContentIdUiModel("soccer"),
          "サッカー"
        ),
        StreamingContentCardUiModel(
          id = StreamingContentIdUiModel("news"),
          "政治ニュース"
        ),
        StreamingContentCardUiModel(
          id = StreamingContentIdUiModel("reality"),
          "恋愛リアリティショー"
        ),
        StreamingContentCardUiModel(
          id = StreamingContentIdUiModel("variety"),
          "お笑いバラエティ"
        ),
      ),
  ) }
}

@Preview(showBackground = true, widthDp = 340)
@Composable
fun MainScreenPortraitPreview() {
  MyStreamTheme { MainScreen(
    cards =
      persistentListOf(
        StreamingContentCardUiModel(
          id = StreamingContentIdUiModel("soccer"),
          "サッカー"
        ),
        StreamingContentCardUiModel(
          id = StreamingContentIdUiModel("news"),
          "政治ニュース"
        ),
        StreamingContentCardUiModel(
          id = StreamingContentIdUiModel("reality"),
          "恋愛リアリティショー"
        ),
        StreamingContentCardUiModel(
          id = StreamingContentIdUiModel("variety"),
          "お笑いバラエティ"
        ),
      ),
  ) }
}
