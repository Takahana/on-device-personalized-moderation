package com.example.mystream.ui.streamingcontent

import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import com.example.mystream.ui.streamingcontent.StreamingContentWatchPageEffect.ShowErrorToast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.mystream.R
import com.example.mystream.service.FilteredLiveChatMessage
import com.example.mystream.service.FilteredLiveChatMessage.HiddenMessage
import com.example.mystream.theme.MyStreamTheme
import com.example.mystream.ui.core.uimodel.StreamingContentIdUiModel
import com.example.mystream.ui.streamingcontent.uimodel.LiveChatMessageUiModel
import com.example.mystream.usecase.StreamingContentWatchPagePresenter
import com.example.mystream.usecase.StreamingContentWatchPageUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun StreamingContentWatchPageScreen(
    viewModel: StreamingContentWatchPageViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val uiState = uiState) {
        is StreamingContentWatchPageUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        is StreamingContentWatchPageUiState.Loaded -> {
            Box(
                modifier = modifier.fillMaxSize(),
            ) {
                var showRegexSheet by rememberSaveable {
                    mutableStateOf(false)
                }
                StreamingContentWatchPageScreen(
                    uiState = uiState,
                    chatInputState = viewModel.chatInputState,
                    onSendPressed = {
                        viewModel.onSendPressed()
                    },
                    onShowRegexSheet = {
                        showRegexSheet = true
                    },
                    modifier = modifier,
                )

                if (showRegexSheet) {
                    RegexPatternsBottomSheet(
                        patterns = uiState.regexPatterns,
                        onDismissRequest = {
                            showRegexSheet = false
                        },
                    )
                }
            }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ShowErrorToast -> {
                    val message = when (effect.errorType) {
                        is ErrorToastType.SendMessageFailed -> {
                            R.string.streaming_content_watch_page_send_message_failed
                        }
                    }
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }
}

@Composable
internal fun StreamingContentWatchPageScreen(
    uiState: StreamingContentWatchPageUiState.Loaded,
    chatInputState: TextFieldState,
    onSendPressed: () -> Unit,
    onShowRegexSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Player(
            id = uiState.contentId,
        )
        Text(
            text = uiState.title,
            style = MaterialTheme.typography.titleLarge,
        )

        ChatList(
            messages = uiState.messages,
            modifier = Modifier.weight(1f),
        )
        ChatInput(
            state = chatInputState,
            modifier = Modifier.fillMaxWidth(),
            onSendPressed = onSendPressed,
            onShowRegexSheet = onShowRegexSheet,
            regexPatternCount = uiState.regexPatterns.size,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegexPatternsBottomSheet(
    patterns: ImmutableList<String>,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "AI Generated Filters",
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${patterns.size} patterns",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                items(patterns) { pattern ->
                    RegexPatternItem(pattern)
                }
            }
        }
    }
}

@Composable
private fun RegexPatternItem(
    pattern: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Text(
            text = pattern,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
            ),
        )
    }
}

@Composable
private fun Player(
    id: StreamingContentIdUiModel,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = id.thumbnail()),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
    )
}

@DrawableRes
private fun StreamingContentIdUiModel.thumbnail(): Int {
    return when (id) {
        "soccer" -> R.drawable.thumb_soccer
        "news" -> R.drawable.thumb_news
        "reality" -> R.drawable.thumb_reality
        "variety" -> R.drawable.thumb_variety
        else -> throw IllegalArgumentException("Unknown content id: ${id}")
    }
}

@Composable
private fun ChatList(
    messages: ImmutableList<LiveChatMessageUiModel>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // スクロール位置を最下部に保つ処理
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo

            if (visibleItems.isNotEmpty()) {
                val lastVisibleItemIndex = visibleItems.last().index
                val totalItemsCount = layoutInfo.totalItemsCount
                val isAtBottom = lastVisibleItemIndex >= totalItemsCount - 2
                if (isAtBottom) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            } else {
                listState.scrollToItem(messages.size - 1)
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(messages) { message ->
            ChatItem(message = message)
        }
    }
}

@Composable
private fun ChatItem(
    message: LiveChatMessageUiModel,
) {
    when (message) {
        is LiveChatMessageUiModel.ShowMessage -> {
            ChatShowItem(message = message)
        }
        is LiveChatMessageUiModel.HiddenMessage -> {
            ChatHiddenItem(message = message)
        }
    }
}

@Composable
private fun ChatShowItem(
    message: LiveChatMessageUiModel.ShowMessage,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
    ){
        Text(
            text = "${message.author}: ${message.message}",
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        )
    }
}

@Composable
private fun ChatHiddenItem(
    message: LiveChatMessageUiModel.HiddenMessage,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.alpha(0.5f),
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var showMessage by remember(message) { mutableStateOf(false) }
            if (showMessage) {
                Text(
                    text = "${message.author}: ${message.message}",
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                )
            } else {
                Text(
                    text = when (message.reason) {
                        HiddenMessage.Reason.BLOCKED_WORD -> stringResource(id = R.string.streaming_content_watch_page_chat_message_hidden)
                        HiddenMessage.Reason.BLOCKED_BY_AI -> stringResource(id = R.string.streaming_content_watch_page_chat_message_hidden_by_ai)
                    },
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                )
            }
            IconButton(
                onClick = { showMessage = !showMessage }
            ) {
                Icon(
                    painter = if (showMessage) {
                        rememberVectorPainter(Icons.Default.VisibilityOff)
                    } else {
                        rememberVectorPainter(Icons.Default.Visibility)
                    },
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun ChatInput(
    state: TextFieldState,
    regexPatternCount: Int ,
    onSendPressed: () -> Unit,
    onShowRegexSheet: () -> Unit ,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            state = state,
            modifier = modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send,
            ),
            onKeyboardAction = {
                onSendPressed()
            },
        )
        IconButton(
            onClick = {
                onShowRegexSheet()
            },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.SettingsInputComponent),
                    contentDescription = null,
                )
                Text(
                    text = regexPatternCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

internal class ChatHiddenItemPreviewParameterProvider :
    PreviewParameterProvider<LiveChatMessageUiModel.HiddenMessage> {
    override val values: Sequence<LiveChatMessageUiModel.HiddenMessage>
        get() = sequenceOf(
            LiveChatMessageUiModel.HiddenMessage(
                author = "Author",
                message = "This is a hidden message.",
                reason = HiddenMessage.Reason.BLOCKED_WORD,
            ),
            LiveChatMessageUiModel.HiddenMessage(
                author = "Author",
                message = "This is a hidden message.",
                reason = HiddenMessage.Reason.BLOCKED_BY_AI,
            ),
        )
}

@Preview
@Composable
private fun ChatHiddenItemPreview(
    @PreviewParameter(ChatHiddenItemPreviewParameterProvider::class) message: LiveChatMessageUiModel.HiddenMessage,
) {
    MyStreamTheme {
        ChatHiddenItem(
            message = message,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StreamingContentWatchPageScreenPreview() {
    MyStreamTheme {
        StreamingContentWatchPageScreen(
            uiState = StreamingContentWatchPageUiState.Loaded(
                contentId = StreamingContentIdUiModel(id = "soccer"),
                title = "Soccer Match",
                messages = listOf(
                    LiveChatMessageUiModel.ShowMessage(
                        author = "Author1",
                        message = "This is a visible message.",
                    ),
                    LiveChatMessageUiModel.HiddenMessage(
                        author = "Author2",
                        message = "This is a hidden message.",
                        reason = HiddenMessage.Reason.BLOCKED_WORD,
                    ),
                ).toImmutableList(),
                regexPatterns = persistentListOf("pattern1", "pattern2"),
            ),
            chatInputState = TextFieldState(),
            onSendPressed = {},
            onShowRegexSheet = {},
        )
    }
}