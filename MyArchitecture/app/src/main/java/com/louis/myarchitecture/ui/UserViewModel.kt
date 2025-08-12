package com.louis.myarchitecture.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.louis.myarchitecture.domain.GetUserUseCase
import com.louis.myarchitecture.data.local.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val uiState: StateFlow<UiState<User>> = _uiState

    init {
        viewModelScope.launch {
            try {
                val user = getUserUseCase().first()
                _uiState.value = UiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}