package com.example.mystream.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystream.data.DataRepository
import com.example.mystream.domain.content.StreamingContentCard
import com.example.mystream.ui.main.MainScreenUiState.Success
import com.example.mystream.ui.main.uimodel.StreamingContentCardUiModel
import com.example.mystream.ui.main.uimodel.mapToUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainScreenViewModel(
  dataRepository: DataRepository,

) : ViewModel() {
  val uiState: StateFlow<MainScreenUiState> =
    dataRepository.cards
      .map<List<StreamingContentCard>, MainScreenUiState> {
        Success(it.mapToUiModel())
      }
      .catch { emit(MainScreenUiState.Error(it)) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainScreenUiState.Loading)
}

sealed interface MainScreenUiState {
  object Loading : MainScreenUiState

  data class Error(val throwable: Throwable) : MainScreenUiState

  data class Success(val cards: ImmutableList<StreamingContentCardUiModel>) : MainScreenUiState
}
