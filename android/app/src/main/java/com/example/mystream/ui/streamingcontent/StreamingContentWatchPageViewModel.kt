package com.example.mystream.ui.streamingcontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystream.domain.chat.LiveChatMessage
import com.example.mystream.logger.Logger
import com.example.mystream.ui.uimodel.id.StreamingContentIdUiModel
import com.example.mystream.usecase.StreamingContentWatchPagePresenter
import com.example.mystream.usecase.StreamingContentWatchPageUseCase
import com.example.mystream.viewmodel.toDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val messages = MutableStateFlow<List<LiveChatMessage>>(emptyList())

    val uiState: StateFlow<StreamingContentWatchPageUiState> = messages.map { currentMessages ->
        StreamingContentWatchPageUiState(
            messages = currentMessages,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StreamingContentWatchPageUiState()
    )

    init {
        logger.d("initialized with streamingContentId: $streamingContentId")
        viewModelScope.launch {
            useCase.display(streamingContentId.toDomain())
        }
    }

    inner class Presenter : StreamingContentWatchPagePresenter {
        override fun newMessage(message: LiveChatMessage) {
            logger.d("new message received: $message")
            messages.update { currentMessages ->
                (currentMessages + message).takeLast(100)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(streamingContentId: StreamingContentIdUiModel): StreamingContentWatchPageViewModel
    }
}