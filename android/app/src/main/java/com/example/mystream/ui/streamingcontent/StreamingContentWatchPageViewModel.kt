package com.example.mystream.ui.streamingcontent

import androidx.lifecycle.ViewModel
import com.example.mystream.logger.Logger
import com.example.mystream.ui.uimodel.id.StreamingContentIdUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = StreamingContentWatchPageViewModel.Factory::class)
class StreamingContentWatchPageViewModel @AssistedInject constructor(
    @Assisted private val streamingContentId: StreamingContentIdUiModel,
) : ViewModel() {

    private val logger = Logger("StreamingContentWatchPageViewModel")

    init {
        logger.d("initialized with streamingContentId: $streamingContentId")
    }

    @AssistedFactory
    interface Factory {
        fun create(streamingContentId: StreamingContentIdUiModel): StreamingContentWatchPageViewModel
    }
}