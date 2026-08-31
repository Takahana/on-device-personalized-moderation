package com.example.mystream.ui.streamingcontent

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.mystream.ui.streamingcontent.StreamingContentWatchPageEffect.ShowErrorToast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.mystream.R
import com.example.mystream.service.FilteredLiveChatMessage
import com.example.mystream.service.FilteredLiveChatMessage.HiddenMessage
import com.example.mystream.theme.MyStreamTheme
import com.example.mystream.ui.streamingcontent.uimodel.LiveChatMessageUiModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun StreamingContentWatchPageScreen(
    viewModel: StreamingContentWatchPageViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Streaming Content Watch Page",
        )

        val uiState by viewModel.uiState.collectAsState()
        ChatList(
            messages = uiState.messages,
            modifier = Modifier.weight(1f),
        )
        ChatInput(
            state = viewModel.chatInputState,
            modifier = Modifier.fillMaxWidth(),
            onSendPressed = {
                viewModel.onSendPressed()
            },
        )
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
    onSendPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        state = state,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send,
        ),
        onKeyboardAction = {
            onSendPressed()
        },
    )
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