package com.example.mystream.ui.streamingcontent

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.logger.Logger
import com.example.mystream.service.FilteredLiveChatMessage
import com.example.mystream.ui.core.uimodel.StreamingContentIdUiModel
import com.example.mystream.ui.core.uimodel.toDomain
import com.example.mystream.ui.streamingcontent.StreamingContentWatchPageEffect.ShowErrorToast
import com.example.mystream.ui.core.viewmodel.mutableEffectFlow
import com.example.mystream.ui.streamingcontent.uimodel.mapToUiModel
import com.example.mystream.usecase.PresentErrorType
import com.example.mystream.usecase.StreamingContentWatchPagePresenter
import com.example.mystream.usecase.StreamingContentWatchPageUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = StreamingContentWatchPageViewModel.Factory::class)
class StreamingContentWatchPageViewModel @AssistedInject constructor(
    @Assisted private val streamingContentId: StreamingContentIdUiModel,
    private val useCaseFactory: StreamingContentWatchPageUseCase.Factory
) : ViewModel() {

    private val logger = Logger("StreamingContentWatchPageViewModel")

    private val useCase: StreamingContentWatchPageUseCase by lazy {
        useCaseFactory.create(Presenter())
    }

    // 上限100件のメッセージを保持する
    private val messages = MutableStateFlow<List<FilteredLiveChatMessage>>(emptyList())

    val uiState: StateFlow<StreamingContentWatchPageUiState> = messages.map { currentMessages ->
        StreamingContentWatchPageUiState(
            messages = currentMessages.mapToUiModel().toImmutableList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StreamingContentWatchPageUiState()
    )

    private val _effect = mutableEffectFlow<StreamingContentWatchPageEffect>()
    val effect: Flow<StreamingContentWatchPageEffect> = _effect.asSharedFlow()

    val chatInputState = TextFieldState()

    init {
        logger.d("initialized with streamingContentId: $streamingContentId")
        viewModelScope.launch {
            useCase.display(streamingContentId.toDomain())
        }
    }

    fun onSendPressed() {
        val messageText = chatInputState.text
        if (messageText.isNotBlank()) {
            viewModelScope.launch {
                useCase.sendMessage(messageText.toString())
            }
        }
    }

    inner class Presenter : StreamingContentWatchPagePresenter {
        override fun newMessage(message: FilteredLiveChatMessage) {
            logger.d("new message received: $message")
            messages.update { currentMessages ->
                (currentMessages + message).takeLast(100)
            }
        }

        override fun clearMessageInput() {
            chatInputState.clearText()
        }

        override fun showError(errorType: PresentErrorType) {
            logger.d("error occurred: $errorType")
            emitErrorEffect(errorType)
        }

        private fun emitErrorEffect(errorType: PresentErrorType) {
            val effect = when (errorType) {
              PresentErrorType.SendMessageFailed -> ShowErrorToast(ErrorToastType.SendMessageFailed)
            }
            viewModelScope.launch {
                _effect.emit(effect)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(streamingContentId: StreamingContentIdUiModel): StreamingContentWatchPageViewModel
    }
}